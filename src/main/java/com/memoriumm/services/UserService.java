package com.memoriumm.services;

import com.memoriumm.dtos.LoginResponseDTO;
import com.memoriumm.dtos.UpdateProfileDTO;
import com.memoriumm.dtos.UserResponseDTO;
import com.memoriumm.entities.User;
import com.memoriumm.enums.PrivacyTypes;
import com.memoriumm.exception.EmailAlreadyExistsException;
import com.memoriumm.exception.InvalidPasswordException;
import com.memoriumm.exception.UserNotFoundException;
import com.memoriumm.exception.UsernameAlreadyExistsException;
import com.memoriumm.mappers.UserMapper;
import com.memoriumm.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.oauth2.jwt.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.time.Instant;
import java.util.*;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final S3Service s3Service;
    private final JwtEncoder jwtEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder,
                       UserMapper mapper, S3Service s3Service, JwtEncoder jwtEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.s3Service = s3Service;
        this.jwtEncoder = jwtEncoder;
    }

    public UserResponseDTO create(String name,
                                  String email,
                                  String username,
                                  String password,
                                  PrivacyTypes privacy) {

        if (repository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }
        if (repository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException();
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setProfilePicture(null);
        user.setPrivacy(privacy);

        User savedUser = repository.save(user);

        return mapper.toDTO(savedUser);
    }


    public LoginResponseDTO login(String login, String password) {

        User user = repository.findByEmailOrUsername(login, login)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciais inválidas"
                ));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciais inválidas"
            );
        }

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getUuid().toString())
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        String accessToken = jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();

        return new LoginResponseDTO(accessToken, "Bearer");
    }


    private void requireOwner(UUID userId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            throw new AccessDeniedException("Acesso não autorizado");
        }

        String authenticatedUserId = jwtAuth.getToken().getSubject();

        if (!userId.toString().equals(authenticatedUserId)) {
            throw new AccessDeniedException(
                    "Você não tem permissão para modificar este perfil"
            );
        }
    }



    public UserResponseDTO findUserById(UUID id) {
        User user = repository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        return mapper.toDTO(user);
    }


    public Set<UserResponseDTO> getAllUserFollowers(UUID id) {
        User user = repository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        Set<User> userFollowers = user.getFollowers();
        Set<UserResponseDTO> userFollowersDTO = new HashSet<>();

        for (User userFollower : userFollowers) {
            UserResponseDTO userFollowerDTO = mapper.toDTO(userFollower);
            userFollowersDTO.add(userFollowerDTO);
        }

        return userFollowersDTO;
    }


    public Set<UserResponseDTO> getAllUserFollowing(UUID id) {
        User user = repository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        Set<User> userFollowing = user.getFollowing();
        Set<UserResponseDTO> userFollowingDTO = new HashSet<>();

        for (User following : userFollowing) {
            UserResponseDTO followingDTO = mapper.toDTO(following);
            userFollowingDTO.add(followingDTO);
        }

        return userFollowingDTO;
    }


    public void updateProfile(
            UUID id,
            UpdateProfileDTO dto,
            boolean bioSended
    ) {

        requireOwner(id);
        User user = repository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (dto.name() != null) {
            user.setName(dto.name());
        }

        if (dto.email() != null
                && !dto.email().equals(user.getEmail())) {

            if (repository.existsByEmail(dto.email())) {
                throw new EmailAlreadyExistsException();
            }

            user.setEmail(dto.email());
        }

        if (dto.username() != null
                && !dto.username().equals(user.getUsername())) {

            if (repository.existsByUsername(dto.username())) {
                throw new UsernameAlreadyExistsException();
            }

            user.setUsername(dto.username());
        }

        if (bioSended) {
            user.setBio(dto.bio());
        }

        if (dto.privacy() != null) {
            user.setPrivacy(dto.privacy());
        }

        repository.save(user);
    }

    public void updatePassword(
            UUID id,
            String currentPassword,
            String newPassword
    ) {

        requireOwner(id);
        User user = repository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidPasswordException();
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        repository.save(user);
    }


    public void updateProfilePicture(UUID id, MultipartFile photo) {

        requireOwner(id);

        User user = repository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        String newPicture = s3Service.uploadProfilePicture(photo);

        user.setProfilePicture(newPicture);

        repository.save(user);
    }



}



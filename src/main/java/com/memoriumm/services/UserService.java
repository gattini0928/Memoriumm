package com.memoriumm.services;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final S3Service s3Service;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder,
                       UserMapper mapper, S3Service s3Service) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.s3Service = s3Service;
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

    public UserResponseDTO findUserById(UUID id) {
        User user = repository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        return mapper.toDTO(user);
    }

    public void updateProfile(
            UUID id,
            UpdateProfileDTO dto,
            boolean bioSended
    ) {
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
        User user = repository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidPasswordException();
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        repository.save(user);
    }


    public void updateProfilePicture(UUID id, MultipartFile photo) {
        User user = repository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        String newPicture = s3Service.uploadProfilePicture(photo);

        user.setProfilePicture(newPicture);

        repository.save(user);
    }

}



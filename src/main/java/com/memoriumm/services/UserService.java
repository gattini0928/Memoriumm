package com.memoriumm.services;

import com.memoriumm.dtos.UserResponseDTO;
import com.memoriumm.entities.User;
import com.memoriumm.enums.PrivacyTypes;
import com.memoriumm.exception.EmailAlreadyExistsException;
import com.memoriumm.exception.UserNotFoundException;
import com.memoriumm.exception.UsernameAlreadyExistsException;
import com.memoriumm.mappers.UserMapper;
import com.memoriumm.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, UserMapper mapper) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    public UserResponseDTO create(String name, String email, String username, String password, String profilePicture, PrivacyTypes privacy) {
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
        user.setProfilePicture(profilePicture);
        user.setPrivacy(privacy);

        User savedUser = repository.save(user);

        return mapper.toDTO(savedUser);
    }

    public UserResponseDTO findUserById(UUID id) {
        User user = repository.findById(id)
                .orElseThrow(()-> new UserNotFoundException());

        UserResponseDTO userDTO = mapper.toDTO(user);
        return  userDTO;
    }
}

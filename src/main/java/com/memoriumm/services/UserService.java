package com.memoriumm.services;

import com.memoriumm.dtos.UserResponseDTO;
import com.memoriumm.entities.User;
import com.memoriumm.enums.PrivacyTypes;
import com.memoriumm.exception.EmailAlreadyExistsException;
import com.memoriumm.exception.UsernameAlreadyExistsException;
import com.memoriumm.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
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

        return new UserResponseDTO(
                savedUser.getUuid(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getUsername(),
                savedUser.getProfilePicture(),
                savedUser.getPrivacy()
        );

    }
}

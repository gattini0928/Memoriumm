package com.memoriumm.mappers;

import com.memoriumm.dtos.UserResponseDTO;
import com.memoriumm.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getUuid(),
                user.getName(),
                user.getEmail(),
                user.getUsername(),
                user.getBio(),
                user.getProfilePicture(),
                user.getPrivacy(),
                user.getCreatedAt(),
                user.getFollowers().size(),
                user.getFollowing().size()
        );
    }
}

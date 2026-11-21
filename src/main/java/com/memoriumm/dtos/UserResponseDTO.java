package com.memoriumm.dtos;

import com.memoriumm.entities.User;
import com.memoriumm.enums.PrivacyTypes;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserResponseDTO(
        UUID uuid,
        String name,
        String email,
        String username,
        String bio,
        String profilePicture,
        PrivacyTypes privacy,
        LocalDateTime createdAt,
        int followersCount,
        int followingCount
) {
}

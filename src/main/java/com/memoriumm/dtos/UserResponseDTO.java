package com.memoriumm.dtos;

import com.memoriumm.enums.PrivacyTypes;

import java.util.UUID;

public record UserResponseDTO(
    UUID uuid,
    String name,
    String email,
    String username,
    String bio,
    String profilePicture,
    PrivacyTypes privacy
) {
}

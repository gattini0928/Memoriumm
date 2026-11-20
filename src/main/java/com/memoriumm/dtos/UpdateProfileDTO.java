package com.memoriumm.dtos;

import com.memoriumm.enums.PrivacyTypes;

public record UpdateProfileDTO(
        String name,
        String email,
        String username,
        String bio,
        PrivacyTypes privacy
) {
}

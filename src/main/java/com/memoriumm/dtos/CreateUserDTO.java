package com.memoriumm.dtos;

import com.memoriumm.enums.PrivacyTypes;

public record CreateUserDTO(
        String name,
        String email,
        String username,
        String password,
        PrivacyTypes privacy
) {}
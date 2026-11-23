package com.memoriumm.dtos;

public record UpdatePasswordDTO(
        String currentPassword,
        String newPassword
) {}

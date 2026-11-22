package com.memoriumm.dtos;

public record LoginResponseDTO(
        String accessToken,
        String tokenType
) {
}


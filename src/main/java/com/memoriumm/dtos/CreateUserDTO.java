package com.memoriumm.dtos;

import com.memoriumm.enums.PrivacyTypes;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserDTO(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100)
        String name,

        @NotBlank(message = "E-mail é obrigatório")
        @Email
        @Size(max = 150)
        String email,

        @NotBlank(message = "Nome de usúario é obrigatório")
        @Size(min = 3, max = 100)
        String username,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8)
        String password,

        PrivacyTypes privacy
) {}

package com.memoriumm.validators;

import com.memoriumm.exception.InvalidUsernameException;

public class UsernameValidator {

    public static void validateUsername(String username) {

        if (username == null || username.isBlank()) {
            throw new InvalidUsernameException(
                    "Nome de usuário é obrigatório"
            );
        }

        if (username.length() < 3 || username.length() > 30) {
            throw new InvalidUsernameException(
                    "Nome de usuário deve ter entre 3 e 30 caracteres"
            );
        }

        if (username.contains("@")) {
            throw new InvalidUsernameException(
                    "Nome de usuário não pode conter @"
            );
        }

        for (int i = 0; i < username.length(); i++) {
            char c = username.charAt(i);

            if (!Character.isLetterOrDigit(c)
                    && c != '_'
                    && c != '.') {
                throw new InvalidUsernameException(
                        "Use apenas letras, números, ponto ou underline"
                );
            }
        }
    }
}

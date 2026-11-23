
package com.memoriumm.validators;

import com.memoriumm.exception.InvalidCreatePasswordException;


public class PasswordValidator {

    public static void validatePassword(String password) {

        if (password == null || password.isBlank()) {
            throw new InvalidCreatePasswordException(
                    "A senha não pode ser vazia"
            );
        }

        if (password.length() < 8) {
            throw new InvalidCreatePasswordException(
                    "Sua senha deve conter no mínimo 8 caracteres"
            );
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (Character.isUpperCase(c)) {
                hasUpper = true;
            }

            if (Character.isLowerCase(c)) {
                hasLower = true;
            }

            if (Character.isDigit(c)) {
                hasDigit = true;
            }

            if (!Character.isLetterOrDigit(c)
                    && !Character.isWhitespace(c)) {
                hasSpecial = true;
            }
        }

        if (!hasUpper) {
            throw new InvalidCreatePasswordException(
                    "Sua senha deve conter pelo menos uma letra maiúscula"
            );
        }

        if (!hasLower) {
            throw new InvalidCreatePasswordException(
                    "Sua senha deve conter pelo menos uma letra minúscula"
            );
        }

        if (!hasDigit) {
            throw new InvalidCreatePasswordException(
                    "Sua senha deve conter pelo menos um número"
            );
        }

        if (!hasSpecial) {
            throw new InvalidCreatePasswordException(
                    "Sua senha deve conter pelo menos um caractere especial"
            );
        }
    }
}

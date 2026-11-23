
package com.memoriumm.validators;

import com.memoriumm.exception.InvalidNameException;

public class NameValidator {

    public static void validateName(String name) {

        if (name == null || name.isBlank()) {
            throw new InvalidNameException(
                    "Seu nome não pode estar vazio"
            );
        }

        // Verifica se contém apenas letras e espaços
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);

            if (!Character.isLetter(c) && c != ' ') {
                throw new InvalidNameException(
                        "Seu nome precisa conter apenas letras"
                );
            }
        }

        // Equivalente ao strings.Fields() do Go
        String[] parts = name.trim().split("\\s+");

        if (parts.length < 2) {
            throw new InvalidNameException(
                    "Digite seu nome e sobrenome"
            );
        }

        if (parts[0].length() < 2 || parts[1].length() < 2) {
            throw new InvalidNameException(
                    "Nome ou sobrenome incompletos"
            );
        }

        if (parts[0].equals(parts[1])) {
            throw new InvalidNameException(
                    "Nome e sobrenome iguais"
            );
        }
    }
}

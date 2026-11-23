package com.memoriumm.validators;

import com.memoriumm.exception.InvalidEmailException;

public class EmailValidator {

    public static void validateEmail(String email) {

        if (email == null || !email.contains("@")) {
            throw new InvalidEmailException();
        }

        String[] parts = email.split("@", -1);

        if (parts.length != 2 || !parts[1].contains(".")) {
            throw new InvalidEmailException();
        }

    }
}
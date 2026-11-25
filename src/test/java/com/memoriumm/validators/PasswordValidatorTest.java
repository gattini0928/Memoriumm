package com.memoriumm.validators;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class PasswordValidatorTest {

    @Test
    void shouldAcceptValidPassword() {

        String password = "Memoriumm@2026";

        assertThatCode(() ->
                PasswordValidator.validatePassword(password)
        ).doesNotThrowAnyException();
    }
}
package com.memoriumm.validators;

import com.memoriumm.exception.InvalidCreatePasswordException;
import com.memoriumm.exception.InvalidEmailException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class EmailValidatorTest {

    @Test
    void shouldAcceptValidEmail() {
        String email = "gabrielgattinidev@gmail.com";

        assertThatCode(() ->
                EmailValidator.validateEmail(email)
        ).doesNotThrowAnyException();

    }
    @Test
    void shouldRejectEmptyEmail() {
        String email = "";

        assertThatThrownBy(() ->
                EmailValidator.validateEmail(email)
        ).isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void shouldRejectNoDomain() {
        String email = "gabrielgattinidev@";

        assertThatThrownBy(() ->
                EmailValidator.validateEmail(email)
        ).isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void shouldRejectNoDotInDomain() {
        String email = "gabrielgattinidev@gmailcom";

        assertThatThrownBy(() ->
                EmailValidator.validateEmail(email)
        ).isInstanceOf(InvalidEmailException.class);
    }
}

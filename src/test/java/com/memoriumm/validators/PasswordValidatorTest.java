package com.memoriumm.validators;

import com.memoriumm.exception.InvalidCreatePasswordException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PasswordValidatorTest {

    @Test
    void shouldAcceptValidPassword() {

        String password = "Memoriumm@2026";

        assertThatCode(() ->
                PasswordValidator.validatePassword(password)
        ).doesNotThrowAnyException();
    }

    @Test
    void minimumLengthInvalidPassword(){
        String password = "Ab@123";

        assertThatThrownBy(() ->
                PasswordValidator.validatePassword(password)
        ).isInstanceOf(InvalidCreatePasswordException.class);
    }

    @Test
    void noUpperCaseInvalidPassword(){
        String password = "invalid@0928";

        assertThatThrownBy(() ->
                PasswordValidator.validatePassword(password)
        ).isInstanceOf(InvalidCreatePasswordException.class);
    }

    @Test
    void noLowerCaseInvalidPassword(){
        String password = "INVALID@0928";

        assertThatThrownBy(() ->
                PasswordValidator.validatePassword(password)
        ).isInstanceOf(InvalidCreatePasswordException.class);
    }

    @Test
    void onlyNumbersInvalidPassword(){
        String password = "092809280928";

        assertThatThrownBy(() ->
                PasswordValidator.validatePassword(password)
        ).isInstanceOf(InvalidCreatePasswordException.class);
    }

    @Test
    void noSpecialCharsInvalidPassword(){
        String password = "Invalid0928";

        assertThatThrownBy(() ->
                PasswordValidator.validatePassword(password)
        ).isInstanceOf(InvalidCreatePasswordException.class);
    }

    @Test
    void shouldRejectPasswordWithoutNumbers() {
        String password = "Memoriumm@";

        assertThatThrownBy(() ->
                PasswordValidator.validatePassword(password)
        ).isInstanceOf(InvalidCreatePasswordException.class);
    }

    @Test
    void shouldRejectEmptyPassword() {
        String password = "";

        assertThatThrownBy(() ->
                PasswordValidator.validatePassword(password)
        ).isInstanceOf(InvalidCreatePasswordException.class);
    }

    @Test
    void shouldRejectNullPassword() {
        assertThatThrownBy(() ->
                PasswordValidator.validatePassword(null)
        ).isInstanceOf(InvalidCreatePasswordException.class);
    }
}
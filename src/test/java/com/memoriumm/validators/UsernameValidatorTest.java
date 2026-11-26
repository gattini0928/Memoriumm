package com.memoriumm.validators;

import com.memoriumm.exception.InvalidCreatePasswordException;
import com.memoriumm.exception.InvalidUsernameException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UsernameValidatorTest {

    @Test
    void shouldAcceptValidUsername(){
        String username = "gattini_0928";

        assertThatCode(() ->
                UsernameValidator.validateUsername(username)
        ).doesNotThrowAnyException();
    }

    @Test
    void shouldRejectEmptyUsername() {
        String username = "";

        assertThatThrownBy(() ->
                UsernameValidator.validateUsername(username)
        ).isInstanceOf(InvalidUsernameException.class);
    }

    @Test
    void shouldRejectMinLengthUsername() {
        String username = "g@";

        assertThatThrownBy(() ->
                UsernameValidator.validateUsername(username)
        ).isInstanceOf(InvalidUsernameException.class);

    }

    @Test
    void shouldRejectMaxLengthUsername() {
        String username = "gattini_092809280928092802909292";

        assertThatThrownBy(() ->
                UsernameValidator.validateUsername(username)
        ).isInstanceOf(InvalidUsernameException.class);
    }

    @Test
    void shouldRejectAtSignInUsername() {
        String username = "gattini@0928";

        assertThatThrownBy(() ->
                UsernameValidator.validateUsername(username)
        ).isInstanceOf(InvalidUsernameException.class);
    }

    @Test
    void shouldRejectSpecialCharsInUsername() {
        String username = "!gattini0928";

        assertThatThrownBy(() ->
                UsernameValidator.validateUsername(username)
        ).isInstanceOf(InvalidUsernameException.class);

    }
}

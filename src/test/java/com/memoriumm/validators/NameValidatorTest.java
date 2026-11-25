package com.memoriumm.validators;

import com.memoriumm.exception.InvalidNameException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class NameValidatorTest {

    @Test
    void shouldAcceptValidName(){

        String name = "Gabriel Gattini";

        assertThatCode(() ->
                NameValidator.validateName(name)
        ).doesNotThrowAnyException();
    }

   @Test
   void shouldRejectEmptyName() {
    String name = "";

    assertThatThrownBy(() ->
            NameValidator.validateName(name)
    ).isInstanceOf(InvalidNameException.class);
   }

    @Test
    void shouldRejectSpacesInName() {
        String name = "G A B R I E L";

        assertThatThrownBy(() ->
                NameValidator.validateName(name)
        ).isInstanceOf(InvalidNameException.class);
    }

    @Test
    void shouldRejectNullName() {
        assertThatThrownBy(() ->
                NameValidator.validateName(null)
        ).isInstanceOf(InvalidNameException.class);
    }

    @Test
    void shouldRejectOnlyOneName(){

        String name = "Gabriel";

        assertThatThrownBy(() -> NameValidator.validateName(name)).
                isInstanceOf(InvalidNameException.class);
    }

    @Test
    void shouldRejectSameName(){
        String name = "Gabriel Gabriel";

        assertThatThrownBy(() -> NameValidator.validateName(name))
                .isInstanceOf(InvalidNameException.class);
    }

    @Test
    void shouldRejectOnlyLetterInFirstName(){
        String name = "G Gattini";

        assertThatThrownBy(() -> NameValidator.validateName(name))
                .isInstanceOf(InvalidNameException.class);
    }

    @Test
    void shouldRejectOnlyLetterInSurname(){
        String name = "Gabriel G";

        assertThatThrownBy(() -> NameValidator.validateName(name))
                .isInstanceOf(InvalidNameException.class);
    }

    @Test
    void shouldAcceptAccentInName(){
        String name = "Júlia Gattini";

        assertThatCode(() -> NameValidator.validateName(name))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldRejectNumbersInName(){
        String name = "Gabriel Gattini0928";

        assertThatThrownBy(() -> NameValidator.validateName(name))
                .isInstanceOf(InvalidNameException.class);
    }
}

package ca.ulaval.glo4003.trotti.domain.user.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidCredentialsExceptionTest {

    @Test
    void whenCreatedWithoutMessage_thenReturnsDefaultMessage() {
        InvalidCredentialsException exception = new InvalidCredentialsException();

        assertThat(exception.getMessage()).isEqualTo("Invalid email or password");
    }

    @Test
    void givenCustomMessage_whenCreated_thenReturnsCustomMessage() {
        InvalidCredentialsException exception = new InvalidCredentialsException("Invalid token");

        assertThat(exception.getMessage()).isEqualTo("Invalid token");
    }
}

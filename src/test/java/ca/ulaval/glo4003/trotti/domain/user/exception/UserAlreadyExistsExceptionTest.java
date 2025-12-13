package ca.ulaval.glo4003.trotti.domain.user.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserAlreadyExistsExceptionTest {

    @Test
    void whenCreated_thenReturnsDefaultMessage() {
        UserAlreadyExistsException exception = new UserAlreadyExistsException();

        assertThat(exception.getMessage()).isEqualTo("Email already in use");
    }
}

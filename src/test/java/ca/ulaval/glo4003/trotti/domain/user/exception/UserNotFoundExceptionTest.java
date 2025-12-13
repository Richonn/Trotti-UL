package ca.ulaval.glo4003.trotti.domain.user.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserNotFoundExceptionTest {

    @Test
    void whenCreated_thenReturnsDefaultMessage() {
        UserNotFoundException exception = new UserNotFoundException();

        assertThat(exception.getMessage()).isEqualTo("User not found");
    }
}

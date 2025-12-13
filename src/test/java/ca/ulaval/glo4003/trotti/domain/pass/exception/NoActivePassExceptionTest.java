package ca.ulaval.glo4003.trotti.domain.pass.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NoActivePassExceptionTest {

    @Test
    void whenCreated_thenReturnsDefaultMessage() {
        NoActivePassException exception = new NoActivePassException();

        assertThat(exception.getMessage()).isEqualTo("No active subscription");
    }
}

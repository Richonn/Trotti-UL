package ca.ulaval.glo4003.trotti.domain.scooter.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScooterNotFoundExceptionTest {

    @Test
    void whenCreated_thenReturnsDefaultMessage() {
        ScooterNotFoundException exception = new ScooterNotFoundException();

        assertThat(exception.getMessage()).isEqualTo("Scooter not found");
    }
}

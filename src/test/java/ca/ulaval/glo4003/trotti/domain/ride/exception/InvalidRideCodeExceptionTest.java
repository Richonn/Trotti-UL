package ca.ulaval.glo4003.trotti.domain.ride.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidRideCodeExceptionTest {

    @Test
    void whenCreated_thenReturnsDefaultMessage() {
        InvalidRideCodeException exception = new InvalidRideCodeException();

        assertThat(exception.getMessage()).isEqualTo("Code is invalid");
    }
}

package ca.ulaval.glo4003.trotti.domain.ride.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExpiredRideCodeExceptionTest {

    @Test
    void whenCreated_thenReturnsDefaultMessage() {
        ExpiredRideCodeException exception = new ExpiredRideCodeException();

        assertThat(exception.getMessage()).isEqualTo("Code is expired");
    }
}

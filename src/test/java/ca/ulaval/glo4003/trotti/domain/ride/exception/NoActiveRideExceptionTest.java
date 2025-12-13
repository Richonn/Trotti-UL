package ca.ulaval.glo4003.trotti.domain.ride.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NoActiveRideExceptionTest {

    @Test
    void whenCreated_thenReturnsDefaultMessage() {
        NoActiveRideException exception = new NoActiveRideException();

        assertThat(exception.getMessage()).isEqualTo("No active ride found for this user");
    }
}

package ca.ulaval.glo4003.trotti.domain.station.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StationNotFoundExceptionTest {

    @Test
    void whenCreated_thenReturnsDefaultMessage() {
        StationNotFoundException exception = new StationNotFoundException();

        assertThat(exception.getMessage()).isEqualTo("Station not found");
    }
}

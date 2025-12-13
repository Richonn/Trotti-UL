package ca.ulaval.glo4003.trotti.domain.station.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StationFullExceptionTest {

    @Test
    void givenStationId_whenCreated_thenReturnsFormattedMessage() {
        StationFullException exception = new StationFullException("VACHON");

        assertThat(exception.getMessage()).isEqualTo("Station VACHON is full");
    }

    @Test
    void givenDifferentStationId_whenCreated_thenReturnsFormattedMessage() {
        StationFullException exception = new StationFullException("PEPS");

        assertThat(exception.getMessage()).isEqualTo("Station PEPS is full");
    }
}

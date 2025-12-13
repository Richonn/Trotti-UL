package ca.ulaval.glo4003.trotti.domain.station.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StationUnderMaintenanceExceptionTest {

    @Test
    void givenStationId_whenCreated_thenReturnsFormattedMessage() {
        StationUnderMaintenanceException exception = new StationUnderMaintenanceException("VACHON");

        assertThat(exception.getMessage()).isEqualTo("Station VACHON is under maintenance");
    }

    @Test
    void givenDifferentStationId_whenCreated_thenReturnsFormattedMessage() {
        StationUnderMaintenanceException exception = new StationUnderMaintenanceException("PEPS");

        assertThat(exception.getMessage()).isEqualTo("Station PEPS is under maintenance");
    }
}

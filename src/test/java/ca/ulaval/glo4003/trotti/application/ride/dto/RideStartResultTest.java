package ca.ulaval.glo4003.trotti.application.ride.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RideStartResultTest {

    @Test
    void givenValidValues_whenCreated_thenReturnsCorrectValues() {
        RideStartResult result = new RideStartResult("ride-123", 85.5);

        assertThat(result.getRideId()).isEqualTo("ride-123");
        assertThat(result.getBatteryLevel()).isEqualTo(85.5);
    }

    @Test
    void givenDifferentBatteryLevel_whenCreated_thenReturnsBatteryLevel() {
        RideStartResult result = new RideStartResult("ride-456", 100.0);

        assertThat(result.getBatteryLevel()).isEqualTo(100.0);
    }
}

package ca.ulaval.glo4003.trotti.domain.scooter.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LowBatteryExceptionTest {

    @Test
    void givenBatteryLevel_whenCreated_thenReturnsFormattedMessage() {
        LowBatteryException exception = new LowBatteryException(15.5);

        assertThat(exception.getMessage()).isEqualTo("Scooter battery too low: 15.5%");
    }

    @Test
    void givenDifferentBatteryLevel_whenCreated_thenReturnsFormattedMessage() {
        LowBatteryException exception = new LowBatteryException(8.2);

        assertThat(exception.getMessage()).isEqualTo("Scooter battery too low: 8.2%");
    }
}

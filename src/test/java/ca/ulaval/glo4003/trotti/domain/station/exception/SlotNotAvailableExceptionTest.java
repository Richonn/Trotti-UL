package ca.ulaval.glo4003.trotti.domain.station.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SlotNotAvailableExceptionTest {

    @Test
    void givenMessage_whenCreated_thenReturnsMessage() {
        SlotNotAvailableException exception = new SlotNotAvailableException("Slot 5 not available");

        assertThat(exception.getMessage()).isEqualTo("Slot 5 not available");
    }

    @Test
    void givenDifferentMessage_whenCreated_thenReturnsDifferentMessage() {
        SlotNotAvailableException exception = new SlotNotAvailableException("Slot occupied");

        assertThat(exception.getMessage()).isEqualTo("Slot occupied");
    }
}

package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.response;

import ca.ulaval.glo4003.trotti.domain.pass.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class EndRideResponseTest {

    @Test
    void givenValidValues_whenCreated_thenReturnsCorrectValues() {
        EndRideResponse response = new EndRideResponse(
                15, 30, new BigDecimal("5.00"), true, new BigDecimal("10.00"), "Ride ended"
        );

        assertThat(response.getDurationMinutes()).isEqualTo(15);
        assertThat(response.getDurationSeconds()).isEqualTo(30);
        assertThat(response.getAdditionalFees()).isEqualByComparingTo(new BigDecimal("5.00"));
        assertThat(response.isChargedImmediately()).isTrue();
        assertThat(response.getMonthlyBalance()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(response.getMessage()).isEqualTo("Ride ended");
    }

    @Test
    void givenNoAdditionalFees_whenCreated_thenReturnsZeroFees() {
        EndRideResponse response = new EndRideResponse(
                10, 0, BigDecimal.ZERO, false, BigDecimal.ZERO, "No fees"
        );

        assertThat(response.getAdditionalFees()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.isChargedImmediately()).isFalse();
    }
}

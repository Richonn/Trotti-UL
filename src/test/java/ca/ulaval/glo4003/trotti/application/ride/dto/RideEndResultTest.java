package ca.ulaval.glo4003.trotti.application.ride.dto;

import ca.ulaval.glo4003.trotti.domain.pass.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class RideEndResultTest {

    @Test
    void givenValidValues_whenCreated_thenReturnsCorrectValues() {
        Money fees = new Money(new BigDecimal("5.00"));
        Money balance = new Money(new BigDecimal("10.00"));
        Duration duration = Duration.ofMinutes(15).plusSeconds(30);

        RideEndResult result = new RideEndResult(
                "ride-123",
                duration,
                fees,
                true,
                balance
        );

        assertThat(result.getRideId()).isEqualTo("ride-123");
        assertThat(result.getRideDuration()).isEqualTo(duration);
        assertThat(result.getAdditionalFees()).isEqualTo(fees);
        assertThat(result.isChargedImmediately()).isTrue();
        assertThat(result.getMonthlyBalanceAfter()).isEqualTo(balance);
        assertThat(result.getDurationInMinutes()).isEqualTo(15);
        assertThat(result.getDurationInSeconds()).isEqualTo(30);
        assertThat(result.getAdditionalFeesAmount()).isEqualByComparingTo(new BigDecimal("5.00"));
    }

    @Test
    void givenNoAdditionalFees_whenCreated_thenReturnsZeroFees() {
        Money fees = Money.zero();
        Money balance = Money.zero();
        Duration duration = Duration.ofMinutes(10);

        RideEndResult result = new RideEndResult(
                "ride-456",
                duration,
                fees,
                false,
                balance
        );

        assertThat(result.getAdditionalFeesAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.isChargedImmediately()).isFalse();
    }

    @Test
    void givenLongDuration_whenCreated_thenCalculatesCorrectly() {
        Duration duration = Duration.ofMinutes(65).plusSeconds(45);

        RideEndResult result = new RideEndResult(
                "ride-789",
                duration,
                Money.zero(),
                false,
                Money.zero()
        );

        assertThat(result.getDurationInMinutes()).isEqualTo(65);
        assertThat(result.getDurationInSeconds()).isEqualTo(45);
    }
}

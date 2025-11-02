package ca.ulaval.glo4003.trotti.domain.ride;

import ca.ulaval.glo4003.trotti.domain.pass.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class RideFeeCalculatorTest {
    private RideFeeCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new RideFeeCalculator();
    }

    @Test
    void givenRideDurationUnder15Minutes_whenCalculatingFees_thenReturnsZero() {
        // Given
        Duration duration = Duration.ofMinutes(14).plusSeconds(59);

        // When
        Money fees = calculator.calculateAdditionalFees(duration);

        // Then
        assertThat(fees.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void givenRideDurationExactly15Minutes_whenCalculatingFees_thenReturnsZero() {
        // Given
        Duration duration = Duration.ofMinutes(15);

        // When
        Money fees = calculator.calculateAdditionalFees(duration);

        // Then
        assertThat(fees.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void givenRideDuration15Minutes14Seconds_whenCalculatingFees_thenReturnsZero() {
        // Given: 15 minutes 14 seconds (under rounding threshold)
        Duration duration = Duration.ofMinutes(15).plusSeconds(14);

        // When
        Money fees = calculator.calculateAdditionalFees(duration);

        // Then
        assertThat(fees.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void givenRideDuration15Minutes15Seconds_whenCalculatingFees_thenRoundsUpToOneMinute() {
        // Given: 15 minutes 15 seconds (at rounding threshold)
        Duration duration = Duration.ofMinutes(15).plusSeconds(15);

        // When
        Money fees = calculator.calculateAdditionalFees(duration);

        // Then: 1 billable minute * $0.075
        assertThat(fees.getAmount()).isEqualByComparingTo(new BigDecimal("0.08"));
    }

    @Test
    void givenRideDuration36Minutes15Seconds_whenCalculatingFees_thenCalculates22BillableMinutes() {
        // Given: 36 minutes 15 seconds
        // Expected: 36 - 15 = 21 minutes, plus 1 for rounding = 22 billable minutes
        Duration duration = Duration.ofMinutes(36).plusSeconds(15);

        // When
        Money fees = calculator.calculateAdditionalFees(duration);

        // Then: 22 * $0.075 = $1.65
        assertThat(fees.getAmount()).isEqualByComparingTo(new BigDecimal("1.65"));
    }

    @Test
    void givenRideDuration22Minutes39Seconds_whenCalculatingFees_thenCalculates8BillableMinutes() {
        // Given: 22 minutes 39 seconds
        // Expected: 22 - 15 = 7 minutes, plus 1 for rounding = 8 billable minutes
        Duration duration = Duration.ofMinutes(22).plusSeconds(39);

        // When
        Money fees = calculator.calculateAdditionalFees(duration);

        // Then: 8 * $0.075 = $0.60
        assertThat(fees.getAmount()).isEqualByComparingTo(new BigDecimal("0.60"));
    }

    @Test
    void givenRideDuration16Minutes_whenCalculatingFees_thenCalculates1BillableMinute() {
        // Given: exactly 16 minutes (no rounding needed)
        Duration duration = Duration.ofMinutes(16);

        // When
        Money fees = calculator.calculateAdditionalFees(duration);

        // Then: 1 * $0.075 = $0.075 rounded to $0.08
        assertThat(fees.getAmount()).isEqualByComparingTo(new BigDecimal("0.08"));
    }

    @Test
    void givenRideDuration30Minutes_whenCalculatingFees_thenCalculates15BillableMinutes() {
        // Given: exactly 30 minutes (no rounding needed)
        Duration duration = Duration.ofMinutes(30);

        // When
        Money fees = calculator.calculateAdditionalFees(duration);

        // Then: 15 * $0.075 = $1.125 rounded to $1.13
        assertThat(fees.getAmount()).isEqualByComparingTo(new BigDecimal("1.13"));
    }

    @Test
    void givenRideDuration60Minutes_whenCalculatingFees_thenCalculates45BillableMinutes() {
        // Given: exactly 60 minutes (1 hour)
        Duration duration = Duration.ofMinutes(60);

        // When
        Money fees = calculator.calculateAdditionalFees(duration);

        // Then: 45 * $0.075 = $3.375 rounded to $3.38
        assertThat(fees.getAmount()).isEqualByComparingTo(new BigDecimal("3.38"));
    }
}

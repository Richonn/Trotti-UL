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
        Duration duration = Duration.ofMinutes(14).plusSeconds(59);

        Money fees = calculator.calculateAdditionalFees(duration);

        assertThat(fees.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void givenRideDurationExactly15Minutes_whenCalculatingFees_thenReturnsZero() {
        Duration duration = Duration.ofMinutes(15);

        Money fees = calculator.calculateAdditionalFees(duration);

        assertThat(fees.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void givenRideDuration15Minutes14Seconds_whenCalculatingFees_thenReturnsZero() {
        Duration duration = Duration.ofMinutes(15).plusSeconds(14);

        Money fees = calculator.calculateAdditionalFees(duration);

        assertThat(fees.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void givenRideDuration15Minutes15Seconds_whenCalculatingFees_thenRoundsUpToOneMinute() {
        Duration duration = Duration.ofMinutes(15).plusSeconds(15);

        Money fees = calculator.calculateAdditionalFees(duration);

        assertThat(fees.getAmount()).isEqualByComparingTo(new BigDecimal("0.08"));
    }

    @Test
    void givenRideDuration36Minutes15Seconds_whenCalculatingFees_thenCalculates22BillableMinutes() {
        Duration duration = Duration.ofMinutes(36).plusSeconds(15);

        Money fees = calculator.calculateAdditionalFees(duration);

        assertThat(fees.getAmount()).isEqualByComparingTo(new BigDecimal("1.65"));
    }

    @Test
    void givenRideDuration22Minutes39Seconds_whenCalculatingFees_thenCalculates8BillableMinutes() {
        Duration duration = Duration.ofMinutes(22).plusSeconds(39);

        Money fees = calculator.calculateAdditionalFees(duration);

        assertThat(fees.getAmount()).isEqualByComparingTo(new BigDecimal("0.60"));
    }

    @Test
    void givenRideDuration16Minutes_whenCalculatingFees_thenCalculates1BillableMinute() {
        Duration duration = Duration.ofMinutes(16);

        Money fees = calculator.calculateAdditionalFees(duration);

        assertThat(fees.getAmount()).isEqualByComparingTo(new BigDecimal("0.08"));
    }

    @Test
    void givenRideDuration30Minutes_whenCalculatingFees_thenCalculates15BillableMinutes() {
        Duration duration = Duration.ofMinutes(30);

        Money fees = calculator.calculateAdditionalFees(duration);

        assertThat(fees.getAmount()).isEqualByComparingTo(new BigDecimal("1.13"));
    }

    @Test
    void givenRideDuration60Minutes_whenCalculatingFees_thenCalculates45BillableMinutes() {
        Duration duration = Duration.ofMinutes(60);

        Money fees = calculator.calculateAdditionalFees(duration);

        assertThat(fees.getAmount()).isEqualByComparingTo(new BigDecimal("3.38"));
    }

    @Test
    void whenGettingQuotaExceededFee_thenReturns5Dollars() {
        Money fee = calculator.getQuotaExceededFee();

        assertThat(fee.getAmount()).isEqualByComparingTo(new BigDecimal("5.00"));
    }
}

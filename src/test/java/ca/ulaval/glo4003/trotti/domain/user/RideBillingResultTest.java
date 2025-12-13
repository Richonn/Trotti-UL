package ca.ulaval.glo4003.trotti.domain.user;

import ca.ulaval.glo4003.trotti.domain.pass.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RideBillingResultTest {

    @Test
    void givenValidValues_whenCreated_thenReturnsCorrectValues() {
        Money fees = new Money(new BigDecimal("10.00"));
        Money balance = new Money(new BigDecimal("25.50"));

        RideBillingResult result = new RideBillingResult(fees, true, balance);

        assertThat(result.getAdditionalFees()).isEqualTo(fees);
        assertThat(result.isChargedImmediately()).isTrue();
        assertThat(result.getMonthlyBalanceAfter()).isEqualTo(balance);
    }

    @Test
    void givenChargedImmediatelyFalse_whenCreated_thenReturnsCorrectValue() {
        Money fees = new Money(new BigDecimal("5.00"));
        Money balance = new Money(new BigDecimal("15.00"));

        RideBillingResult result = new RideBillingResult(fees, false, balance);

        assertThat(result.isChargedImmediately()).isFalse();
    }

    @Test
    void givenZeroFees_whenCreated_thenReturnsZero() {
        Money zeroFees = Money.zero();
        Money balance = new Money(new BigDecimal("0.00"));

        RideBillingResult result = new RideBillingResult(zeroFees, false, balance);

        assertThat(result.getAdditionalFees().getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}

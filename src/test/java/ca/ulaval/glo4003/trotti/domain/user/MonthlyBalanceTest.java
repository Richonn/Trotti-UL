package ca.ulaval.glo4003.trotti.domain.user;

import ca.ulaval.glo4003.trotti.domain.pass.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MonthlyBalanceTest {

    @Test
    void givenNewMonthlyBalance_whenCreated_thenAmountIsZero() {
        MonthlyBalance balance = new MonthlyBalance();

        assertThat(balance.getAmount().getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void givenMonthlyBalanceWithAmount_whenCreated_thenAmountIsSet() {
        Money amount = new Money(new BigDecimal("10.50"));

        MonthlyBalance balance = new MonthlyBalance(amount);

        assertThat(balance.getAmount()).isEqualTo(amount);
    }

    @Test
    void givenMonthlyBalance_whenAddingAmount_thenReturnsNewBalanceWithAddedAmount() {
        MonthlyBalance balance = new MonthlyBalance(new Money(new BigDecimal("5.00")));
        Money toAdd = new Money(new BigDecimal("3.50"));

        MonthlyBalance newBalance = balance.add(toAdd);

        assertThat(newBalance.getAmount().getAmount()).isEqualByComparingTo(new BigDecimal("8.50"));
        assertThat(balance.getAmount().getAmount()).isEqualByComparingTo(new BigDecimal("5.00"));
    }

    @Test
    void givenMonthlyBalance_whenAddingZero_thenBalanceRemainsUnchanged() {
        MonthlyBalance balance = new MonthlyBalance(new Money(new BigDecimal("10.00")));

        MonthlyBalance newBalance = balance.add(Money.zero());

        assertThat(newBalance.getAmount().getAmount()).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void givenTwoMonthlyBalancesWithSameAmount_whenCompared_thenAreEqual() {
        Money amount = new Money(new BigDecimal("15.75"));
        MonthlyBalance balance1 = new MonthlyBalance(amount);
        MonthlyBalance balance2 = new MonthlyBalance(new Money(new BigDecimal("15.75")));

        assertThat(balance1).isEqualTo(balance2);
        assertThat(balance1.hashCode()).isEqualTo(balance2.hashCode());
    }

    @Test
    void givenTwoMonthlyBalancesWithDifferentAmounts_whenCompared_thenAreNotEqual() {
        MonthlyBalance balance1 = new MonthlyBalance(new Money(new BigDecimal("10.00")));
        MonthlyBalance balance2 = new MonthlyBalance(new Money(new BigDecimal("20.00")));

        assertThat(balance1).isNotEqualTo(balance2);
    }
}

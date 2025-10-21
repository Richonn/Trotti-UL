package ca.ulaval.glo4003.trotti.domain.pass;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void givenValidBigDecimalAmount_whenCreatingMoney_thenSucceeds() {
        Money money = new Money(BigDecimal.valueOf(100.50));

        assertEquals(BigDecimal.valueOf(100.50), money.getAmount());
    }

    @Test
    void givenValidDoubleAmount_whenCreatingMoney_thenSucceeds() {
        Money money = new Money(50.75);

        assertEquals(BigDecimal.valueOf(50.75), money.getAmount());
    }

    @Test
    void givenZeroAmount_whenCreatingMoney_thenSucceeds() {
        Money money = new Money(BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, money.getAmount());
    }

    @Test
    void givenNullAmount_whenCreatingMoney_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Money((BigDecimal) null));
    }

    @Test
    void givenNegativeAmount_whenCreatingMoney_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Money(BigDecimal.valueOf(-10.50)));
    }

    @Test
    void givenZeroFactory_whenCreatingMoney_thenReturnsZero() {
        Money money = Money.zero();

        assertEquals(BigDecimal.ZERO, money.getAmount());
    }

    @Test
    void givenTwoMoneyAmounts_whenAdding_thenReturnsSum() {
        Money money1 = new Money(50.0);
        Money money2 = new Money(25.0);

        Money result = money1.add(money2);

        assertEquals(BigDecimal.valueOf(75.0), result.getAmount());
    }

    @Test
    void givenMoneyAndIntMultiplier_whenMultiplying_thenReturnsProduct() {
        Money money = new Money(25.0);

        Money result = money.multiply(3);

        assertEquals(BigDecimal.valueOf(75.0), result.getAmount());
    }

    @Test
    void givenMoneyAndBigDecimalMultiplier_whenMultiplying_thenReturnsProduct() {
        Money money = new Money(100.0);

        Money result = money.multiply(BigDecimal.valueOf(1.5));

        assertEquals(0, new BigDecimal("150.0").compareTo(result.getAmount()));
    }

    @Test
    void givenTwoMoneyAmounts_whenComparingEqual_thenReturnsZero() {
        Money money1 = new Money(50.0);
        Money money2 = new Money(50.0);

        int comparison = money1.compareTo(money2);

        assertEquals(0, comparison);
    }

    @Test
    void givenTwoMoneyAmounts_whenFirstIsLarger_thenReturnsPositive() {
        Money money1 = new Money(100.0);
        Money money2 = new Money(50.0);

        int comparison = money1.compareTo(money2);

        assertTrue(comparison > 0);
    }

    @Test
    void givenTwoMoneyAmounts_whenFirstIsSmaller_thenReturnsNegative() {
        Money money1 = new Money(25.0);
        Money money2 = new Money(50.0);

        int comparison = money1.compareTo(money2);

        assertTrue(comparison < 0);
    }

    @Test
    void givenTwoMoneyWithSameAmount_whenComparingEquality_thenAreEqual() {
        Money money1 = new Money(50.0);
        Money money2 = new Money(50.0);

        assertEquals(money1, money2);
        assertEquals(money1.hashCode(), money2.hashCode());
    }

    @Test
    void givenTwoMoneyWithDifferentAmounts_whenComparingEquality_thenAreNotEqual() {
        Money money1 = new Money(50.0);
        Money money2 = new Money(75.0);

        assertNotEquals(money1, money2);
    }

    @Test
    void givenMoney_whenConvertingToString_thenReturnsAmountAsString() {
        Money money = new Money(123.45);

        String result = money.toString();

        assertEquals("123.45", result);
    }
}

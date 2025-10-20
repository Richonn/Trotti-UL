package ca.ulaval.glo4003.trotti.domain.pass;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardNumberTest {

    @Test
    void givenValidCardNumber_whenCreatingCardNumber_thenSucceeds() {
        CardNumber cardNumber = new CardNumber("4532015112830366");

        assertEquals("4532015112830366", cardNumber.getValue());
    }

    @Test
    void givenInvalidCardNumber_whenCreatingCardNumber_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CardNumber("1234567890123456"));
    }

    @Test
    void givenCardNumberTooShort_whenCreatingCardNumber_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CardNumber("1234567"));
    }

    @Test
    void givenCardNumberTooLong_whenCreatingCardNumber_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CardNumber("12345678901234567"));
    }

    @Test
    void givenCardNumberWithNonDigits_whenCreatingCardNumber_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CardNumber("4532-0151-1283-0366"));
        assertThrows(IllegalArgumentException.class, () -> new CardNumber("453201511283036a"));
    }

    @Test
    void givenEmptyCardNumber_whenCreatingCardNumber_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CardNumber(""));
        assertThrows(IllegalArgumentException.class, () -> new CardNumber("   "));
    }

    @Test
    void givenNullCardNumber_whenCreatingCardNumber_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CardNumber(null));
    }

    @Test
    void givenValidCardNumber_whenConvertingToString_thenMasksAllButLastFourDigits() {
        CardNumber cardNumber = new CardNumber("4532015112830366");

        assertEquals("****0366", cardNumber.toString());
    }

    @Test
    void givenTwoCardNumbersWithSameValue_whenComparingEquality_thenAreEqual() {
        CardNumber cardNumber1 = new CardNumber("4532015112830366");
        CardNumber cardNumber2 = new CardNumber("4532015112830366");

        assertEquals(cardNumber1, cardNumber2);
        assertEquals(cardNumber1.hashCode(), cardNumber2.hashCode());
    }

    @Test
    void givenTwoCardNumbersWithDifferentValues_whenComparingEquality_thenAreNotEqual() {
        CardNumber cardNumber1 = new CardNumber("4532015112830366");
        CardNumber cardNumber2 = new CardNumber("5425233430109903");

        assertNotEquals(cardNumber1, cardNumber2);
    }

    @Test
    void givenAnotherValidCardNumber_whenCreatingCardNumber_thenSucceeds() {
        CardNumber cardNumber = new CardNumber("5425233430109903");

        assertEquals("5425233430109903", cardNumber.getValue());
    }
}

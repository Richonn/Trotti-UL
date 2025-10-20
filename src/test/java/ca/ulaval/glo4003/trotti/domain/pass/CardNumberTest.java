package ca.ulaval.glo4003.trotti.domain.pass;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardNumberTest {

    @Test
    void givenValidCardNumber_whenCreatingCardNumber_thenSucceeds() {
        // Arrange & Act
        CardNumber cardNumber = new CardNumber("4532015112830366");

        // Assert
        assertEquals("4532015112830366", cardNumber.getValue());
    }

    @Test
    void givenInvalidCardNumber_whenCreatingCardNumber_thenThrowsException() {
        // Act & Assert - Luhn algorithm fails
        assertThrows(IllegalArgumentException.class, () -> new CardNumber("1234567890123456"));
    }

    @Test
    void givenCardNumberTooShort_whenCreatingCardNumber_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new CardNumber("1234567"));
    }

    @Test
    void givenCardNumberTooLong_whenCreatingCardNumber_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new CardNumber("12345678901234567"));
    }

    @Test
    void givenCardNumberWithNonDigits_whenCreatingCardNumber_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new CardNumber("4532-0151-1283-0366"));
        assertThrows(IllegalArgumentException.class, () -> new CardNumber("453201511283036a"));
    }

    @Test
    void givenEmptyCardNumber_whenCreatingCardNumber_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new CardNumber(""));
        assertThrows(IllegalArgumentException.class, () -> new CardNumber("   "));
    }

    @Test
    void givenNullCardNumber_whenCreatingCardNumber_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new CardNumber(null));
    }

    @Test
    void givenValidCardNumber_whenConvertingToString_thenMasksAllButLastFourDigits() {
        // Arrange
        CardNumber cardNumber = new CardNumber("4532015112830366");

        // Act & Assert
        assertEquals("****0366", cardNumber.toString());
    }

    @Test
    void givenTwoCardNumbersWithSameValue_whenComparingEquality_thenAreEqual() {
        // Arrange
        CardNumber cardNumber1 = new CardNumber("4532015112830366");
        CardNumber cardNumber2 = new CardNumber("4532015112830366");

        // Act & Assert
        assertEquals(cardNumber1, cardNumber2);
        assertEquals(cardNumber1.hashCode(), cardNumber2.hashCode());
    }

    @Test
    void givenTwoCardNumbersWithDifferentValues_whenComparingEquality_thenAreNotEqual() {
        // Arrange
        CardNumber cardNumber1 = new CardNumber("4532015112830366");
        CardNumber cardNumber2 = new CardNumber("5425233430109903");

        // Act & Assert
        assertNotEquals(cardNumber1, cardNumber2);
    }

    @Test
    void givenAnotherValidCardNumber_whenCreatingCardNumber_thenSucceeds() {
        // Arrange & Act - Test with different valid card
        CardNumber cardNumber = new CardNumber("5425233430109903");

        // Assert
        assertEquals("5425233430109903", cardNumber.getValue());
    }
}

package ca.ulaval.glo4003.trotti.domain.pass;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class ExpiryDateTest {

    @Test
    void givenValidFutureDate_whenCreatingExpiryDate_thenSucceeds() {
        // Arrange - Use a date 2 years in the future
        int futureYear = (Year.now().getValue() + 2) % 100;
        String dateString = String.format("12/%02d", futureYear);

        // Act
        ExpiryDate expiryDate = new ExpiryDate(dateString);

        // Assert
        assertEquals(dateString, expiryDate.getValue());
    }

    @Test
    void givenCurrentMonthAndYear_whenCreatingExpiryDate_thenSucceeds() {
        // Arrange
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear() % 100;
        String dateString = String.format("%02d/%02d", currentMonth, currentYear);

        // Act
        ExpiryDate expiryDate = new ExpiryDate(dateString);

        // Assert
        assertEquals(dateString, expiryDate.getValue());
    }

    @Test
    void givenPastDate_whenCreatingExpiryDate_thenThrowsException() {
        // Arrange - Use a date from 2 years ago
        int pastYear = (Year.now().getValue() - 2) % 100;
        String dateString = String.format("12/%02d", pastYear);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new ExpiryDate(dateString));
    }

    @Test
    void givenInvalidFormat_whenCreatingExpiryDate_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new ExpiryDate("12/2025"));
        assertThrows(IllegalArgumentException.class, () -> new ExpiryDate("12-25"));
        assertThrows(IllegalArgumentException.class, () -> new ExpiryDate("1225"));
    }

    @Test
    void givenInvalidMonth_whenCreatingExpiryDate_thenThrowsException() {
        // Arrange
        int futureYear = (Year.now().getValue() + 1) % 100;

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> new ExpiryDate(String.format("00/%02d", futureYear)));
        assertThrows(IllegalArgumentException.class,
            () -> new ExpiryDate(String.format("13/%02d", futureYear)));
    }

    @Test
    void givenEmptyString_whenCreatingExpiryDate_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new ExpiryDate(""));
        assertThrows(IllegalArgumentException.class, () -> new ExpiryDate("   "));
    }

    @Test
    void givenNull_whenCreatingExpiryDate_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new ExpiryDate(null));
    }

    @Test
    void givenTwoExpiryDatesWithSameValue_whenComparingEquality_thenAreEqual() {
        // Arrange
        int futureYear = (Year.now().getValue() + 1) % 100;
        String dateString = String.format("12/%02d", futureYear);
        ExpiryDate date1 = new ExpiryDate(dateString);
        ExpiryDate date2 = new ExpiryDate(dateString);

        // Act & Assert
        assertEquals(date1, date2);
        assertEquals(date1.hashCode(), date2.hashCode());
    }

    @Test
    void givenValidExpiryDate_whenConvertingToString_thenReturnsValue() {
        // Arrange
        int futureYear = (Year.now().getValue() + 1) % 100;
        String dateString = String.format("12/%02d", futureYear);
        ExpiryDate expiryDate = new ExpiryDate(dateString);

        // Act & Assert
        assertEquals(dateString, expiryDate.toString());
    }
}

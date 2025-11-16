package ca.ulaval.glo4003.trotti.domain.pass;

import ca.ulaval.glo4003.trotti.infrastructure.time.FixedClock;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class ExpiryDateTest {

    @Test
    void givenValidFutureDate_whenCreatingExpiryDate_thenSucceeds() {
        int futureYear = (Year.now().getValue() + 2) % 100;
        String dateString = String.format("12/%02d", futureYear);

        ExpiryDate expiryDate = new ExpiryDate(dateString);

        assertEquals(dateString, expiryDate.getValue());
    }

    @Test
    void givenCurrentMonthAndYear_whenCreatingExpiryDate_thenSucceeds() {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear() % 100;
        String dateString = String.format("%02d/%02d", currentMonth, currentYear);

        ExpiryDate expiryDate = new ExpiryDate(dateString);

        assertEquals(dateString, expiryDate.getValue());
    }

    @Test
    void givenPastDate_whenCheckingIfExpired_thenReturnsTrue() {
        LocalDate today = LocalDate.of(2024, 1, 15);
        FixedClock clock = new FixedClock(today);
        String dateString = "12/22";

        ExpiryDate expiryDate = new ExpiryDate(dateString);

        assertTrue(expiryDate.isExpired(clock));
    }

    @Test
    void givenInvalidFormat_whenCreatingExpiryDate_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new ExpiryDate("12/2025"));
        assertThrows(IllegalArgumentException.class, () -> new ExpiryDate("12-25"));
        assertThrows(IllegalArgumentException.class, () -> new ExpiryDate("1225"));
    }

    @Test
    void givenInvalidMonth_whenCreatingExpiryDate_thenThrowsException() {
        int futureYear = (Year.now().getValue() + 1) % 100;

        assertThrows(IllegalArgumentException.class,
            () -> new ExpiryDate(String.format("00/%02d", futureYear)));
        assertThrows(IllegalArgumentException.class,
            () -> new ExpiryDate(String.format("13/%02d", futureYear)));
    }

    @Test
    void givenEmptyString_whenCreatingExpiryDate_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new ExpiryDate(""));
        assertThrows(IllegalArgumentException.class, () -> new ExpiryDate("   "));
    }

    @Test
    void givenNull_whenCreatingExpiryDate_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new ExpiryDate(null));
    }

    @Test
    void givenTwoExpiryDatesWithSameValue_whenComparingEquality_thenAreEqual() {
        int futureYear = (Year.now().getValue() + 1) % 100;
        String dateString = String.format("12/%02d", futureYear);
        ExpiryDate date1 = new ExpiryDate(dateString);
        ExpiryDate date2 = new ExpiryDate(dateString);

        assertEquals(date1, date2);
        assertEquals(date1.hashCode(), date2.hashCode());
    }

    @Test
    void givenValidExpiryDate_whenConvertingToString_thenReturnsValue() {
        int futureYear = (Year.now().getValue() + 1) % 100;
        String dateString = String.format("12/%02d", futureYear);
        ExpiryDate expiryDate = new ExpiryDate(dateString);

        assertEquals(dateString, expiryDate.toString());
    }
}

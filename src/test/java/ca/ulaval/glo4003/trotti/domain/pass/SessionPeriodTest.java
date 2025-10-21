package ca.ulaval.glo4003.trotti.domain.pass;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SessionPeriodTest {

    @Test
    void givenAutumnSessionCode_whenCreating_thenHasCorrectDates() {
        SessionPeriod period = SessionPeriod.fromCode("A2025");

        assertEquals("A2025", period.getCode());
        assertEquals(LocalDate.of(2025, 9, 1), period.getStartDate());
        assertEquals(LocalDate.of(2026, 1, 1), period.getEndDate());
        assertEquals(4, period.getDurationInMonths());
    }

    @Test
    void givenWinterSessionCode_whenCreating_thenHasCorrectDates() {
        SessionPeriod period = SessionPeriod.fromCode("H2026");

        assertEquals("H2026", period.getCode());
        assertEquals(LocalDate.of(2026, 1, 1), period.getStartDate());
        assertEquals(LocalDate.of(2026, 5, 1), period.getEndDate());
        assertEquals(4, period.getDurationInMonths());
    }

    @Test
    void givenSummerSessionCode_whenCreating_thenHasCorrectDates() {
        SessionPeriod period = SessionPeriod.fromCode("E2025");

        assertEquals("E2025", period.getCode());
        assertEquals(LocalDate.of(2025, 5, 1), period.getStartDate());
        assertEquals(LocalDate.of(2025, 9, 1), period.getEndDate());
        assertEquals(4, period.getDurationInMonths());
    }

    @Test
    void givenAnnualSessionCode_whenCreating_thenHasCorrectDates() {
        SessionPeriod period = SessionPeriod.fromCode("AN2025");

        assertEquals("AN2025", period.getCode());
        assertEquals(LocalDate.of(2025, 9, 1), period.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 1), period.getEndDate());
        assertEquals(12, period.getDurationInMonths());
    }

    @Test
    void givenNullCode_whenCreating_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> SessionPeriod.fromCode(null));
    }

    @Test
    void givenEmptyCode_whenCreating_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> SessionPeriod.fromCode(""));
    }

    @Test
    void givenBlankCode_whenCreating_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> SessionPeriod.fromCode("   "));
    }

    @Test
    void givenInvalidPrefix_whenCreating_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> SessionPeriod.fromCode("Z2025"));
    }

    @Test
    void givenInvalidYearFormat_whenCreating_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> SessionPeriod.fromCode("AINVALID"));
    }

    @Test
    void givenDateWithinPeriod_whenCheckingContains_thenReturnsTrue() {
        SessionPeriod period = SessionPeriod.fromCode("A2025");
        LocalDate dateInPeriod = LocalDate.of(2025, 10, 15);

        boolean contains = period.contains(dateInPeriod);

        assertTrue(contains);
    }

    @Test
    void givenDateOnStartDate_whenCheckingContains_thenReturnsTrue() {
        SessionPeriod period = SessionPeriod.fromCode("A2025");
        LocalDate startDate = LocalDate.of(2025, 9, 1);

        boolean contains = period.contains(startDate);

        assertTrue(contains);
    }

    @Test
    void givenDateOnEndDate_whenCheckingContains_thenReturnsTrue() {
        SessionPeriod period = SessionPeriod.fromCode("A2025");
        LocalDate endDate = LocalDate.of(2026, 1, 1);

        boolean contains = period.contains(endDate);

        assertTrue(contains);
    }

    @Test
    void givenDateBeforePeriod_whenCheckingContains_thenReturnsFalse() {
        SessionPeriod period = SessionPeriod.fromCode("A2025");
        LocalDate dateBefore = LocalDate.of(2025, 8, 31);

        boolean contains = period.contains(dateBefore);

        assertFalse(contains);
    }

    @Test
    void givenDateAfterPeriod_whenCheckingContains_thenReturnsFalse() {
        SessionPeriod period = SessionPeriod.fromCode("A2025");
        LocalDate dateAfter = LocalDate.of(2026, 1, 2);

        boolean contains = period.contains(dateAfter);

        assertFalse(contains);
    }

    @Test
    void givenTwoPeriodsWithSameCode_whenComparingEquality_thenAreEqual() {
        SessionPeriod period1 = SessionPeriod.fromCode("A2025");
        SessionPeriod period2 = SessionPeriod.fromCode("A2025");

        assertEquals(period1, period2);
        assertEquals(period1.hashCode(), period2.hashCode());
    }

    @Test
    void givenTwoPeriodsWithDifferentCodes_whenComparingEquality_thenAreNotEqual() {
        SessionPeriod period1 = SessionPeriod.fromCode("A2025");
        SessionPeriod period2 = SessionPeriod.fromCode("H2026");

        assertNotEquals(period1, period2);
    }

    @Test
    void givenSessionPeriod_whenConvertingToString_thenReturnsCode() {
        SessionPeriod period = SessionPeriod.fromCode("A2025");

        String result = period.toString();

        assertEquals("A2025", result);
    }
}

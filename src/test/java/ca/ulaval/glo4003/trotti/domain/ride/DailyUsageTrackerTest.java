package ca.ulaval.glo4003.trotti.domain.ride;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DailyUsageTrackerTest {
    private DailyUsageTracker tracker;
    private String userId;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        tracker = new DailyUsageTracker();
        userId = "user-123";
        today = LocalDate.of(2025, 1, 15);
    }

    @Test
    void givenNoUsage_whenGetUsageForDay_thenReturnsZero() {
        int usage = tracker.getUsageForDay(userId, today);

        assertThat(usage).isEqualTo(0);
    }

    @Test
    void givenUsageRecorded_whenGetUsageForDay_thenReturnsCorrectAmount() {
        tracker.recordUsage(userId, today, 20);

        int usage = tracker.getUsageForDay(userId, today);

        assertThat(usage).isEqualTo(20);
    }

    @Test
    void givenMultipleUsages_whenGetUsageForDay_thenReturnsSum() {
        tracker.recordUsage(userId, today, 10);
        tracker.recordUsage(userId, today, 15);
        tracker.recordUsage(userId, today, 5);

        int usage = tracker.getUsageForDay(userId, today);

        assertThat(usage).isEqualTo(30);
    }

    @Test
    void givenUsageOnDifferentDays_whenGetUsageForDay_thenReturnsOnlyTodayUsage() {
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);

        tracker.recordUsage(userId, yesterday, 10);
        tracker.recordUsage(userId, today, 20);
        tracker.recordUsage(userId, tomorrow, 15);

        int usage = tracker.getUsageForDay(userId, today);

        assertThat(usage).isEqualTo(20);
    }

    @Test
    void givenMultipleUsers_whenGetUsageForDay_thenReturnsOnlyUserUsage() {
        String otherUserId = "user-456";

        tracker.recordUsage(userId, today, 20);
        tracker.recordUsage(otherUserId, today, 30);

        int usage = tracker.getUsageForDay(userId, today);

        assertThat(usage).isEqualTo(20);
    }

    @Test
    void givenUsageRecorded_whenReset_thenUsageIsZero() {
        tracker.recordUsage(userId, today, 20);
        tracker.reset(userId, today);

        int usage = tracker.getUsageForDay(userId, today);

        assertThat(usage).isEqualTo(0);
    }

    @Test
    void givenUsageOnMultipleDays_whenResetOneDay_thenOtherDaysUnaffected() {
        LocalDate yesterday = today.minusDays(1);

        tracker.recordUsage(userId, yesterday, 10);
        tracker.recordUsage(userId, today, 20);

        tracker.reset(userId, today);

        assertThat(tracker.getUsageForDay(userId, today)).isEqualTo(0);
        assertThat(tracker.getUsageForDay(userId, yesterday)).isEqualTo(10);
    }
}

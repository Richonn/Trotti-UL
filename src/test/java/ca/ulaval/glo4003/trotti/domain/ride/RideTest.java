package ca.ulaval.glo4003.trotti.domain.ride;

import ca.ulaval.glo4003.trotti.infrastructure.time.FixedClock;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RideTest {

    @Test
    void givenValidData_whenStartingRide_thenSucceeds() {
        String userId = "user-123";
        String scooterId = "scooter-456";
        LocalDateTime startTime = LocalDateTime.now();

        Ride ride = Ride.start(userId, scooterId, startTime);

        assertNotNull(ride.getId());
        assertEquals(userId, ride.getUserId());
        assertEquals(scooterId, ride.getScooterId());
        assertEquals(startTime, ride.getStartTime());
        assertNull(ride.getEndTime());
        assertEquals(RideStatus.ACTIVE, ride.getStatus());
    }

    @Test
    void givenActiveRide_whenEnding_thenRideIsCompleted() {
        Ride ride = Ride.start("user-123", "scooter-456", LocalDateTime.now().minusMinutes(10));
        LocalDateTime endTime = LocalDateTime.now();

        ride.end(endTime);

        assertEquals(endTime, ride.getEndTime());
        assertEquals(RideStatus.COMPLETED, ride.getStatus());
    }

    @Test
    void givenCompletedRide_whenEndingAgain_thenThrowsException() {
        Ride ride = Ride.start("user-123", "scooter-456", LocalDateTime.now().minusMinutes(10));
        ride.end(LocalDateTime.now());

        assertThrows(IllegalStateException.class, () -> ride.end(LocalDateTime.now()));
    }

    @Test
    void givenEndTimeBeforeStartTime_whenEnding_thenThrowsException() {
        LocalDateTime startTime = LocalDateTime.now();
        Ride ride = Ride.start("user-123", "scooter-456", startTime);
        LocalDateTime endTime = startTime.minusMinutes(5);

        assertThrows(IllegalArgumentException.class, () -> ride.end(endTime));
    }

    @Test
    void givenActiveRide_whenGettingDuration_thenReturnsTimeSinceStart() {
        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 12, 0);
        LocalDateTime startTime = now.minusMinutes(10);
        FixedClock clock = new FixedClock(now);
        Ride ride = Ride.start("user-123", "scooter-456", startTime);

        Duration duration = ride.getDuration(clock);

        assertEquals(10, duration.toMinutes());
    }

    @Test
    void givenCompletedRide_whenGettingDuration_thenReturnsActualDuration() {
        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 12, 0);
        LocalDateTime startTime = now.minusMinutes(30);
        LocalDateTime endTime = now.minusMinutes(10);
        FixedClock clock = new FixedClock(now);
        Ride ride = Ride.start("user-123", "scooter-456", startTime);
        ride.end(endTime);

        Duration duration = ride.getDuration(clock);

        assertEquals(20, duration.toMinutes());
    }

    @Test
    void givenTwoRidesWithDifferentIds_whenComparingEquality_thenAreNotEqual() {
        Ride ride1 = Ride.start("user-123", "scooter-456", LocalDateTime.now());
        Ride ride2 = Ride.start("user-123", "scooter-456", LocalDateTime.now());

        assertNotEquals(ride1, ride2);
    }
}

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
        String stationId = "VACHON";
        LocalDateTime startTime = LocalDateTime.now();

        Ride ride = Ride.start(userId, scooterId, stationId, startTime);

        assertNotNull(ride.getId());
        assertEquals(userId, ride.getUserId());
        assertEquals(scooterId, ride.getScooterId());
        assertEquals(stationId, ride.getStartStationId());
        assertEquals(startTime, ride.getStartTime());
        assertNull(ride.getEndTime());
        assertEquals(RideStatus.ACTIVE, ride.getStatus());
    }

    @Test
    void givenActiveRide_whenEnding_thenRideIsCompleted() {
        Ride ride = Ride.start("user-123", "scooter-456", "VACHON", LocalDateTime.now().minusMinutes(10));
        LocalDateTime endTime = LocalDateTime.now();
        String endStation = "PEPS";

        ride.end(endTime, endStation);

        assertEquals(endTime, ride.getEndTime());
        assertEquals(endStation, ride.getEndStationId());
        assertEquals(RideStatus.COMPLETED, ride.getStatus());
    }

    @Test
    void givenCompletedRide_whenEndingAgain_thenThrowsException() {
        Ride ride = Ride.start("user-123", "scooter-456", "VACHON", LocalDateTime.now().minusMinutes(10));
        ride.end(LocalDateTime.now(), "PEPS");

        assertThrows(IllegalStateException.class, () -> ride.end(LocalDateTime.now(), "DESJARDINS"));
    }

    @Test
    void givenEndTimeBeforeStartTime_whenEnding_thenThrowsException() {
        LocalDateTime startTime = LocalDateTime.now();
        Ride ride = Ride.start("user-123", "scooter-456", "VACHON", startTime);
        LocalDateTime endTime = startTime.minusMinutes(5);

        assertThrows(IllegalArgumentException.class, () -> ride.end(endTime, "PEPS"));
    }

    @Test
    void givenActiveRide_whenGettingDuration_thenReturnsTimeSinceStart() {
        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 12, 0);
        LocalDateTime startTime = now.minusMinutes(10);
        FixedClock clock = new FixedClock(now);
        Ride ride = Ride.start("user-123", "scooter-456", "VACHON", startTime);

        Duration duration = ride.getDuration(clock);

        assertEquals(10, duration.toMinutes());
    }

    @Test
    void givenCompletedRide_whenGettingDuration_thenReturnsActualDuration() {
        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 12, 0);
        LocalDateTime startTime = now.minusMinutes(30);
        LocalDateTime endTime = now.minusMinutes(10);
        FixedClock clock = new FixedClock(now);
        Ride ride = Ride.start("user-123", "scooter-456", "VACHON", startTime);
        ride.end(endTime, "PEPS");

        Duration duration = ride.getDuration(clock);

        assertEquals(20, duration.toMinutes());
    }

    @Test
    void givenTwoRidesWithDifferentIds_whenComparingEquality_thenAreNotEqual() {
        Ride ride1 = Ride.start("user-123", "scooter-456", "VACHON", LocalDateTime.now());
        Ride ride2 = Ride.start("user-123", "scooter-456", "VACHON", LocalDateTime.now());

        assertNotEquals(ride1, ride2);
    }
}

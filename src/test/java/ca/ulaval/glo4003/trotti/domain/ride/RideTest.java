package ca.ulaval.glo4003.trotti.domain.ride;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RideTest {

    @Test
    void givenValidData_whenStartingRide_thenSucceeds() {
        String userId = "user-123";
        String scooterId = "scooter-456";
        String startStationId = "station-1";
        LocalDateTime startTime = LocalDateTime.now();

        Ride ride = Ride.start(userId, scooterId, startStationId, startTime);

        assertNotNull(ride.getId());
        assertEquals(userId, ride.getUserId());
        assertEquals(scooterId, ride.getScooterId());
        assertEquals(startStationId, ride.getStartStationId());
        assertEquals(startTime, ride.getStartTime());
        assertNull(ride.getEndTime());
        assertNull(ride.getEndStationId());
        assertEquals(RideStatus.ACTIVE, ride.getStatus());
    }

    @Test
    void givenActiveRide_whenEnding_thenRideIsCompleted() {
        Ride ride = Ride.start("user-123", "scooter-456", "station-1", LocalDateTime.now().minusMinutes(10));
        LocalDateTime endTime = LocalDateTime.now();
        String endStationId = "station-2";

        ride.end(endTime, endStationId);

        assertEquals(endTime, ride.getEndTime());
        assertEquals(endStationId, ride.getEndStationId());
        assertEquals(RideStatus.COMPLETED, ride.getStatus());
    }

    @Test
    void givenCompletedRide_whenEndingAgain_thenThrowsException() {
        Ride ride = Ride.start("user-123", "scooter-456", "station-1", LocalDateTime.now().minusMinutes(10));
        ride.end(LocalDateTime.now(), "station-2");

        assertThrows(IllegalStateException.class, () -> ride.end(LocalDateTime.now(), "station-3"));
    }

    @Test
    void givenEndTimeBeforeStartTime_whenEnding_thenThrowsException() {
        LocalDateTime startTime = LocalDateTime.now();
        Ride ride = Ride.start("user-123", "scooter-456", "station-1", startTime);
        LocalDateTime endTime = startTime.minusMinutes(5);

        assertThrows(IllegalArgumentException.class, () -> ride.end(endTime, "station-2"));
    }

    @Test
    void givenActiveRide_whenGettingDuration_thenReturnsTimeSinceStart() {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(10);
        Ride ride = Ride.start("user-123", "scooter-456", "station-1", startTime);

        Duration duration = ride.getDuration();

        assertTrue(duration.toMinutes() >= 9 && duration.toMinutes() <= 11);
    }

    @Test
    void givenCompletedRide_whenGettingDuration_thenReturnsActualDuration() {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(30);
        LocalDateTime endTime = LocalDateTime.now().minusMinutes(10);
        Ride ride = Ride.start("user-123", "scooter-456", "station-1", startTime);
        ride.end(endTime, "station-2");

        Duration duration = ride.getDuration();

        assertEquals(20, duration.toMinutes());
    }

    @Test
    void givenTwoRidesWithDifferentIds_whenComparingEquality_thenAreNotEqual() {
        Ride ride1 = Ride.start("user-123", "scooter-456", "station-1", LocalDateTime.now());
        Ride ride2 = Ride.start("user-123", "scooter-456", "station-1", LocalDateTime.now());

        assertNotEquals(ride1, ride2);
    }
}

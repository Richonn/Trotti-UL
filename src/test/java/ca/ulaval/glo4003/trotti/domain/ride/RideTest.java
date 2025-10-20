package ca.ulaval.glo4003.trotti.domain.ride;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RideTest {

    @Test
    void givenValidData_whenStartingRide_thenSucceeds() {
        // Arrange
        String userId = "user-123";
        String scooterId = "scooter-456";
        LocalDateTime startTime = LocalDateTime.now();

        // Act
        Ride ride = Ride.start(userId, scooterId, startTime);

        // Assert
        assertNotNull(ride.getId());
        assertEquals(userId, ride.getUserId());
        assertEquals(scooterId, ride.getScooterId());
        assertEquals(startTime, ride.getStartTime());
        assertNull(ride.getEndTime());
        assertEquals(RideStatus.ACTIVE, ride.getStatus());
    }

    @Test
    void givenActiveRide_whenEnding_thenRideIsCompleted() {
        // Arrange
        Ride ride = Ride.start("user-123", "scooter-456", LocalDateTime.now().minusMinutes(10));
        LocalDateTime endTime = LocalDateTime.now();

        // Act
        ride.end(endTime);

        // Assert
        assertEquals(endTime, ride.getEndTime());
        assertEquals(RideStatus.COMPLETED, ride.getStatus());
    }

    @Test
    void givenCompletedRide_whenEndingAgain_thenThrowsException() {
        // Arrange
        Ride ride = Ride.start("user-123", "scooter-456", LocalDateTime.now().minusMinutes(10));
        ride.end(LocalDateTime.now());

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> ride.end(LocalDateTime.now()));
    }

    @Test
    void givenEndTimeBeforeStartTime_whenEnding_thenThrowsException() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now();
        Ride ride = Ride.start("user-123", "scooter-456", startTime);
        LocalDateTime endTime = startTime.minusMinutes(5);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> ride.end(endTime));
    }

    @Test
    void givenActiveRide_whenGettingDuration_thenReturnsTimeSinceStart() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(10);
        Ride ride = Ride.start("user-123", "scooter-456", startTime);

        // Act
        Duration duration = ride.getDuration();

        // Assert - Should be approximately 10 minutes
        assertTrue(duration.toMinutes() >= 9 && duration.toMinutes() <= 11);
    }

    @Test
    void givenCompletedRide_whenGettingDuration_thenReturnsActualDuration() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(30);
        LocalDateTime endTime = LocalDateTime.now().minusMinutes(10);
        Ride ride = Ride.start("user-123", "scooter-456", startTime);
        ride.end(endTime);

        // Act
        Duration duration = ride.getDuration();

        // Assert - Should be exactly 20 minutes
        assertEquals(20, duration.toMinutes());
    }

    @Test
    void givenTwoRidesWithDifferentIds_whenComparingEquality_thenAreNotEqual() {
        // Arrange
        Ride ride1 = Ride.start("user-123", "scooter-456", LocalDateTime.now());
        Ride ride2 = Ride.start("user-123", "scooter-456", LocalDateTime.now());

        // Act & Assert
        assertNotEquals(ride1, ride2);
    }
}

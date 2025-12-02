package ca.ulaval.glo4003.trotti.infrastructure.persistence.memory;

import ca.ulaval.glo4003.trotti.domain.ride.Ride;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryRideRepositoryTest {

    private InMemoryRideRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRideRepository();
    }

    @Test
    void givenCompletedRidesInDateRange_whenFindingByUserIdInDateRange_thenReturnsMatchingRides() {
        String userId = "user-123";
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 31, 23, 59, 59);

        Ride rideInRange1 = Ride.start(userId, "scooter-1", "VACHON", LocalDateTime.of(2025, 1, 10, 10, 0));
        rideInRange1.end(LocalDateTime.of(2025, 1, 10, 10, 30), "PEPS");

        Ride rideInRange2 = Ride.start(userId, "scooter-2", "PEPS", LocalDateTime.of(2025, 1, 20, 14, 0));
        rideInRange2.end(LocalDateTime.of(2025, 1, 20, 14, 15), "VACHON");

        Ride rideOutOfRange = Ride.start(userId, "scooter-3", "VACHON", LocalDateTime.of(2024, 12, 31, 23, 0));
        rideOutOfRange.end(LocalDateTime.of(2024, 12, 31, 23, 30), "PEPS");

        repository.save(rideInRange1);
        repository.save(rideInRange2);
        repository.save(rideOutOfRange);

        List<Ride> result = repository.findCompletedRidesByUserIdInDateRange(userId, startDate, endDate);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(rideInRange1, rideInRange2);
    }

    @Test
    void givenNoRidesInDateRange_whenFindingByUserIdInDateRange_thenReturnsEmptyList() {
        String userId = "user-123";
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 31, 23, 59, 59);

        Ride rideOutOfRange = Ride.start(userId, "scooter-1", "VACHON", LocalDateTime.of(2024, 12, 31, 23, 0));
        rideOutOfRange.end(LocalDateTime.of(2024, 12, 31, 23, 30), "PEPS");

        repository.save(rideOutOfRange);

        List<Ride> result = repository.findCompletedRidesByUserIdInDateRange(userId, startDate, endDate);

        assertThat(result).isEmpty();
    }

    @Test
    void givenRideAtStartBoundary_whenFindingByUserIdInDateRange_thenReturnsRide() {
        String userId = "user-123";
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 31, 23, 59, 59);

        Ride rideAtStart = Ride.start(userId, "scooter-1", "VACHON", LocalDateTime.of(2025, 1, 1, 0, 0));
        rideAtStart.end(LocalDateTime.of(2025, 1, 1, 0, 30), "PEPS");

        repository.save(rideAtStart);

        List<Ride> result = repository.findCompletedRidesByUserIdInDateRange(userId, startDate, endDate);

        assertThat(result).hasSize(1);
        assertThat(result).contains(rideAtStart);
    }

    @Test
    void givenRideAtEndBoundary_whenFindingByUserIdInDateRange_thenReturnsRide() {
        String userId = "user-123";
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 31, 23, 59, 59);

        Ride rideAtEnd = Ride.start(userId, "scooter-1", "VACHON", LocalDateTime.of(2025, 1, 31, 23, 59, 59));
        rideAtEnd.end(LocalDateTime.of(2025, 2, 1, 0, 10), "PEPS");

        repository.save(rideAtEnd);

        List<Ride> result = repository.findCompletedRidesByUserIdInDateRange(userId, startDate, endDate);

        assertThat(result).hasSize(1);
        assertThat(result).contains(rideAtEnd);
    }

    @Test
    void givenMultipleUsers_whenFindingByUserIdInDateRange_thenReturnsOnlySpecifiedUserRides() {
        String userId1 = "user-123";
        String userId2 = "user-456";
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 31, 23, 59, 59);

        Ride user1Ride = Ride.start(userId1, "scooter-1", "VACHON", LocalDateTime.of(2025, 1, 10, 10, 0));
        user1Ride.end(LocalDateTime.of(2025, 1, 10, 10, 30), "PEPS");

        Ride user2Ride = Ride.start(userId2, "scooter-2", "PEPS", LocalDateTime.of(2025, 1, 10, 11, 0));
        user2Ride.end(LocalDateTime.of(2025, 1, 10, 11, 30), "VACHON");

        repository.save(user1Ride);
        repository.save(user2Ride);

        List<Ride> result = repository.findCompletedRidesByUserIdInDateRange(userId1, startDate, endDate);

        assertThat(result).hasSize(1);
        assertThat(result).contains(user1Ride);
    }

    @Test
    void givenActiveRides_whenFindingByUserIdInDateRange_thenExcludesActiveRides() {
        String userId = "user-123";
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 31, 23, 59, 59);

        Ride completedRide = Ride.start(userId, "scooter-1", "VACHON", LocalDateTime.of(2025, 1, 10, 10, 0));
        completedRide.end(LocalDateTime.of(2025, 1, 10, 10, 30), "PEPS");

        Ride activeRide = Ride.start(userId, "scooter-2", "PEPS", LocalDateTime.of(2025, 1, 15, 11, 0));

        repository.save(completedRide);
        repository.save(activeRide);

        List<Ride> result = repository.findCompletedRidesByUserIdInDateRange(userId, startDate, endDate);

        assertThat(result).hasSize(1);
        assertThat(result).contains(completedRide);
    }
}

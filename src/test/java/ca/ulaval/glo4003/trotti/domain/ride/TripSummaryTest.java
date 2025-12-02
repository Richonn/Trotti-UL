package ca.ulaval.glo4003.trotti.domain.ride;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TripSummaryTest {

    @Test
    void givenEmptyRideList_whenCreatingSummary_thenReturnsZeroValues() {
        List<Ride> rides = new ArrayList<>();

        TripSummary summary = TripSummary.from(rides);

        assertThat(summary.getTotalTrips()).isZero();
        assertThat(summary.getTotalDurationMinutes()).isZero();
        assertThat(summary.getAverageDurationMinutes()).isZero();
        assertThat(summary.getFavoriteStationId()).isNull();
    }

    @Test
    void givenSingleRide_whenCreatingSummary_thenCalculatesCorrectValues() {
        Ride ride = Ride.start("user-1", "scooter-1", "VACHON", LocalDateTime.of(2025, 1, 10, 10, 0));
        ride.end(LocalDateTime.of(2025, 1, 10, 10, 30), "PEPS");

        TripSummary summary = TripSummary.from(Arrays.asList(ride));

        assertThat(summary.getTotalTrips()).isEqualTo(1);
        assertThat(summary.getTotalDurationMinutes()).isEqualTo(30);
        assertThat(summary.getAverageDurationMinutes()).isEqualTo(30);
    }

    @Test
    void givenMultipleRides_whenCreatingSummary_thenCalculatesTotalDuration() {
        Ride ride1 = Ride.start("user-1", "scooter-1", "VACHON", LocalDateTime.of(2025, 1, 10, 10, 0));
        ride1.end(LocalDateTime.of(2025, 1, 10, 10, 30), "PEPS");

        Ride ride2 = Ride.start("user-1", "scooter-2", "PEPS", LocalDateTime.of(2025, 1, 11, 14, 0));
        ride2.end(LocalDateTime.of(2025, 1, 11, 14, 45), "VACHON");

        Ride ride3 = Ride.start("user-1", "scooter-3", "VACHON", LocalDateTime.of(2025, 1, 12, 9, 0));
        ride3.end(LocalDateTime.of(2025, 1, 12, 9, 15), "PEPS");

        TripSummary summary = TripSummary.from(Arrays.asList(ride1, ride2, ride3));

        assertThat(summary.getTotalTrips()).isEqualTo(3);
        assertThat(summary.getTotalDurationMinutes()).isEqualTo(90);
        assertThat(summary.getAverageDurationMinutes()).isEqualTo(30);
    }

    @Test
    void givenMultipleRides_whenCreatingSummary_thenCalculatesAverageDuration() {
        Ride ride1 = Ride.start("user-1", "scooter-1", "VACHON", LocalDateTime.of(2025, 1, 10, 10, 0));
        ride1.end(LocalDateTime.of(2025, 1, 10, 10, 20), "PEPS");

        Ride ride2 = Ride.start("user-1", "scooter-2", "PEPS", LocalDateTime.of(2025, 1, 11, 14, 0));
        ride2.end(LocalDateTime.of(2025, 1, 11, 14, 40), "VACHON");

        TripSummary summary = TripSummary.from(Arrays.asList(ride1, ride2));

        assertThat(summary.getTotalTrips()).isEqualTo(2);
        assertThat(summary.getTotalDurationMinutes()).isEqualTo(60);
        assertThat(summary.getAverageDurationMinutes()).isEqualTo(30);
    }

    @Test
    void givenRidesWithSameStation_whenCreatingSummary_thenIdentifiesFavoriteStation() {
        Ride ride1 = Ride.start("user-1", "scooter-1", "VACHON", LocalDateTime.of(2025, 1, 10, 10, 0));
        ride1.end(LocalDateTime.of(2025, 1, 10, 10, 30), "PEPS");

        Ride ride2 = Ride.start("user-1", "scooter-2", "VACHON", LocalDateTime.of(2025, 1, 11, 14, 0));
        ride2.end(LocalDateTime.of(2025, 1, 11, 14, 45), "DESJARDINS");

        Ride ride3 = Ride.start("user-1", "scooter-3", "PEPS", LocalDateTime.of(2025, 1, 12, 9, 0));
        ride3.end(LocalDateTime.of(2025, 1, 12, 9, 15), "VACHON");

        TripSummary summary = TripSummary.from(Arrays.asList(ride1, ride2, ride3));

        assertThat(summary.getFavoriteStationId()).isEqualTo("VACHON");
    }

    @Test
    void givenRidesWithDifferentStations_whenCreatingSummary_thenSelectsMostFrequentStation() {
        Ride ride1 = Ride.start("user-1", "scooter-1", "VACHON", LocalDateTime.of(2025, 1, 10, 10, 0));
        ride1.end(LocalDateTime.of(2025, 1, 10, 10, 30), "PEPS");

        Ride ride2 = Ride.start("user-1", "scooter-2", "PEPS", LocalDateTime.of(2025, 1, 11, 14, 0));
        ride2.end(LocalDateTime.of(2025, 1, 11, 14, 45), "PEPS");

        Ride ride3 = Ride.start("user-1", "scooter-3", "DESJARDINS", LocalDateTime.of(2025, 1, 12, 9, 0));
        ride3.end(LocalDateTime.of(2025, 1, 12, 9, 15), "PEPS");

        TripSummary summary = TripSummary.from(Arrays.asList(ride1, ride2, ride3));

        assertThat(summary.getFavoriteStationId()).isEqualTo("PEPS");
    }

    @Test
    void givenRidesWithEqualFrequency_whenCreatingSummary_thenSelectsOneStation() {
        Ride ride1 = Ride.start("user-1", "scooter-1", "VACHON", LocalDateTime.of(2025, 1, 10, 10, 0));
        ride1.end(LocalDateTime.of(2025, 1, 10, 10, 30), "PEPS");

        Ride ride2 = Ride.start("user-1", "scooter-2", "PEPS", LocalDateTime.of(2025, 1, 11, 14, 0));
        ride2.end(LocalDateTime.of(2025, 1, 11, 14, 45), "VACHON");

        TripSummary summary = TripSummary.from(Arrays.asList(ride1, ride2));

        assertThat(summary.getFavoriteStationId()).isIn("VACHON", "PEPS");
    }

    @Test
    void givenRideWithStartAndEndAtSameStation_whenCreatingSummary_thenCountsBothOccurrences() {
        Ride ride1 = Ride.start("user-1", "scooter-1", "VACHON", LocalDateTime.of(2025, 1, 10, 10, 0));
        ride1.end(LocalDateTime.of(2025, 1, 10, 10, 30), "VACHON");

        Ride ride2 = Ride.start("user-1", "scooter-2", "PEPS", LocalDateTime.of(2025, 1, 11, 14, 0));
        ride2.end(LocalDateTime.of(2025, 1, 11, 14, 45), "DESJARDINS");

        TripSummary summary = TripSummary.from(Arrays.asList(ride1, ride2));

        assertThat(summary.getFavoriteStationId()).isEqualTo("VACHON");
    }
}

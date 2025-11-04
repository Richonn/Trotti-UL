package ca.ulaval.glo4003.trotti.infrastructure.persistence.memory;

import ca.ulaval.glo4003.trotti.domain.ride.Trip;
import ca.ulaval.glo4003.trotti.domain.ride.TripRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryTripRepository implements TripRepository {
    private final Map<String, Trip> trips = new ConcurrentHashMap<>();

    @Override
    public void save(Trip trip) {
        trips.put(trip.getId(), trip);
    }

    @Override
    public Optional<Trip> findById(String id) {
        return Optional.ofNullable(trips.get(id));
    }

    @Override
    public List<Trip> findByUserId(String userId) {
        return trips.values().stream()
                .filter(trip -> trip.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public long getTotalMinutesUsedToday(String userId, LocalDate date) {
        return trips.values().stream()
                .filter(trip -> trip.getUserId().equals(userId))
                .filter(trip -> trip.getStartTime().toLocalDate().equals(date))
                .mapToLong(trip -> trip.getDuration().toMinutes())
                .sum();
    }
}

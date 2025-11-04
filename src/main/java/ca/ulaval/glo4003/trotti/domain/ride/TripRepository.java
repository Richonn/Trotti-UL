package ca.ulaval.glo4003.trotti.domain.ride;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TripRepository {
    void save(Trip trip);

    Optional<Trip> findById(String id);

    List<Trip> findByUserId(String userId);

    long getTotalMinutesUsedToday(String userId, LocalDate date);
}

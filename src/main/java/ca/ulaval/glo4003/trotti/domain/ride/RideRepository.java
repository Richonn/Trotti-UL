package ca.ulaval.glo4003.trotti.domain.ride;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RideRepository {
    void save(Ride ride);

    Optional<Ride> findById(String id);

    Optional<Ride> findActiveRideByUserId(String userId);

    List<Ride> findCompletedRidesByUserId(String userId);

    List<Ride> findCompletedRidesByUserIdInDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate);
}

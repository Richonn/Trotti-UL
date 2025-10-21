package ca.ulaval.glo4003.trotti.domain.ride;

import java.util.Optional;

public interface RideRepository {
    void save(Ride ride);

    Optional<Ride> findById(String id);

    Optional<Ride> findActiveRideByUserId(String userId);
}

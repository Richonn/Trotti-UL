package ca.ulaval.glo4003.trotti.infrastructure.persistence.memory;

import ca.ulaval.glo4003.trotti.domain.ride.Ride;
import ca.ulaval.glo4003.trotti.domain.ride.RideRepository;
import ca.ulaval.glo4003.trotti.domain.ride.RideStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryRideRepository implements RideRepository {
    private final Map<String, Ride> rides = new ConcurrentHashMap<>();

    @Override
    public void save(Ride ride) {
        rides.put(ride.getId(), ride);
    }

    @Override
    public Optional<Ride> findById(String id) {
        return Optional.ofNullable(rides.get(id));
    }

    @Override
    public Optional<Ride> findActiveRideByUserId(String userId) {
        return rides.values().stream()
                .filter(ride -> ride.getUserId().equals(userId))
                .filter(ride -> ride.getStatus() == RideStatus.ACTIVE)
                .findFirst();
    }

    @Override
    public List<Ride> findByUserId(String userId) {
        return rides.values().stream()
                .filter(ride -> ride.getUserId().equals(userId))
                .filter(ride -> ride.getStatus() == RideStatus.COMPLETED)
                .collect(Collectors.toList());
    }
}

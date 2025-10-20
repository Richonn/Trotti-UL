package ca.ulaval.glo4003.trotti.infrastructure.persistence.memory;

import ca.ulaval.glo4003.trotti.domain.ride.RideCode;
import ca.ulaval.glo4003.trotti.domain.ride.RideCodeRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRideCodeRepository implements RideCodeRepository {
    private final Map<String, RideCode> rideCodesByEmail = new ConcurrentHashMap<>();

    @Override
    public void save(String userEmail, RideCode rideCode) {
        rideCodesByEmail.put(userEmail, rideCode);
    }

    @Override
    public Optional<RideCode> findByUserEmail(String userEmail) {
        return Optional.ofNullable(rideCodesByEmail.get(userEmail));
    }

    @Override
    public void delete(String userEmail) {
        rideCodesByEmail.remove(userEmail);
    }
}

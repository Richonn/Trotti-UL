package ca.ulaval.glo4003.trotti.domain.ride;

import java.util.Optional;

public interface RideCodeRepository {
    void save(String userEmail, RideCode rideCode);
    Optional<RideCode> findByUserEmail(String userEmail);
    void delete(String userEmail);
    void deleteAll();
}

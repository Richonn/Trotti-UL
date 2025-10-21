package ca.ulaval.glo4003.trotti.domain.scooter;

import java.util.List;
import java.util.Optional;

public interface ScooterRepository {
    void save(Scooter scooter);

    Optional<Scooter> findById(String id);

    List<Scooter> findByStationId(String stationId);

    Optional<Scooter> findByStationIdAndSlotNumber(String stationId, int slotNumber);
}

package ca.ulaval.glo4003.trotti.infrastructure.persistence.memory;

import ca.ulaval.glo4003.trotti.domain.scooter.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryScooterRepository implements ScooterRepository {
    private final Map<String, Scooter> scooters = new ConcurrentHashMap<>();

    @Override
    public void save(Scooter scooter) {
        scooters.put(scooter.getId(), scooter);
    }

    @Override
    public Optional<Scooter> findById(String id) {
        return Optional.ofNullable(scooters.get(id));
    }

    @Override
    public List<Scooter> findByStationId(String stationId) {
        return scooters.values().stream()
                .filter(scooter -> scooter.getCurrentStationId().isPresent())
                .filter(scooter -> scooter.getCurrentStationId().get().equals(stationId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Scooter> findByStationIdAndSlotNumber(String stationId, int slotNumber) {
        return scooters.values().stream()
                .filter(scooter -> scooter.getCurrentStationId().isPresent())
                .filter(scooter -> scooter.getCurrentStationId().get().equals(stationId))
                .filter(scooter -> scooter.getSlotNumber().isPresent())
                .filter(scooter -> scooter.getSlotNumber().get() == slotNumber)
                .findFirst();
    }

    public void initializeScootersForStation(String stationId, int capacity) {
        int numberOfScooters = (int) (capacity * 0.8);
        for (int i = 0; i < numberOfScooters; i++) {
            Scooter scooter = Scooter.create(stationId, i);
            save(scooter);
        }
    }
}

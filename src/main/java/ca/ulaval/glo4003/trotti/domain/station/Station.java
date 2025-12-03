package ca.ulaval.glo4003.trotti.domain.station;

import ca.ulaval.glo4003.trotti.domain.scooter.Scooter;
import ca.ulaval.glo4003.trotti.domain.station.exception.SlotNotAvailableException;
import ca.ulaval.glo4003.trotti.domain.station.exception.StationFullException;
import ca.ulaval.glo4003.trotti.domain.station.exception.StationUnderMaintenanceException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Station {
    private final String id;
    private final String location;
    private final int capacity;
    private StationState state;
    private final List<Scooter> scooters;

    public Station(String id, String location, int capacity) {
        this.id = Objects.requireNonNull(id, "Station ID cannot be null");
        this.location = Objects.requireNonNull(location, "Location cannot be null");
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.scooters = new ArrayList<>();
    }

    public void startMaintenance() {
        this.state = StationState.UNDER_MAINTENANCE;
    }

    public void endMaintenance() {
        this.state = StationState.OPERATIONAL;
    }

    public StationState getState() {
        return state;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public Scooter rentScooter(int slotNumber, String userId) {
        if (this.state == StationState.UNDER_MAINTENANCE) {
            throw new StationUnderMaintenanceException(this.id);
        }

        Scooter scooter = findScooterAtSlot(slotNumber)
                .orElseThrow(() -> new SlotNotAvailableException("Scooter not found at slot " + slotNumber));

        scooter.rentTo(userId);
        scooters.remove(scooter);
        return scooter;
    }

    public void returnScooter(Scooter scooter, int slotNumber) {
        if (isFull()) {
            throw new StationFullException(this.id);
        }
        scooter.returnToStation(this.id, slotNumber);
        scooters.add(scooter);
    }

    public List<Scooter> getAvailableScooters() {
        return scooters.stream()
                .filter(Scooter::isAvailable)
                .collect(Collectors.toList());
    }

    public List<Scooter> getScooters() {
        return Collections.unmodifiableList(scooters);
    }

    public void addScooter(Scooter scooter) {
        this.scooters.add(scooter);
    }

    public boolean isFull() {
        return scooters.size() >= capacity;
    }

    private java.util.Optional<Scooter> findScooterAtSlot(int slotNumber) {
        return scooters.stream()
                .filter(s -> s.getSlotNumber().isPresent() && s.getSlotNumber().get() == slotNumber)
                .findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

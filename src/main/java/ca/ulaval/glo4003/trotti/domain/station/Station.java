package ca.ulaval.glo4003.trotti.domain.station;

import java.util.Objects;

public class Station {
    private final String id;
    private final String location;
    private final int capacity;
    private StationState state;

    public Station(String id, String location, int capacity) {
        this.id = Objects.requireNonNull(id, "Station ID cannot be null");
        this.location = Objects.requireNonNull(location, "Location cannot be null");
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
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

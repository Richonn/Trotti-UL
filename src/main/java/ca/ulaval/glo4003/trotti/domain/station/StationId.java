package ca.ulaval.glo4003.trotti.domain.station;

import java.util.Objects;

public class StationId {
    private final String value;

    public StationId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Station ID cannot be empty");
        }
        this.value = value.toUpperCase();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationId stationId = (StationId) o;
        return Objects.equals(value, stationId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}

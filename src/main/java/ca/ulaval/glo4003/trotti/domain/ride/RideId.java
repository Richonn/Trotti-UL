package ca.ulaval.glo4003.trotti.domain.ride;

import java.util.Objects;
import java.util.UUID;

public class RideId {
    private final UUID value;

    private RideId(UUID value) {
        this.value = Objects.requireNonNull(value, "RideId cannot be null");
    }

    public static RideId generate() {
        return new RideId(UUID.randomUUID());
    }

    public static RideId fromString(String value) {
        return new RideId(UUID.fromString(value));
    }

    public String getValue() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RideId rideId = (RideId) o;
        return Objects.equals(value, rideId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

package ca.ulaval.glo4003.trotti.domain.scooter;

import java.util.Objects;
import java.util.UUID;

public class ScooterId {
    private final UUID value;

    private ScooterId(UUID value) {
        this.value = Objects.requireNonNull(value, "ScooterId cannot be null");
    }

    public static ScooterId generate() {
        return new ScooterId(UUID.randomUUID());
    }

    public static ScooterId fromString(String value) {
        return new ScooterId(UUID.fromString(value));
    }

    public String getValue() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScooterId scooterId = (ScooterId) o;
        return Objects.equals(value, scooterId.value);
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

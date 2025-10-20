package ca.ulaval.glo4003.trotti.domain.pass;

import java.util.Objects;
import java.util.UUID;

public class PassId {
    private final UUID value;

    private PassId(UUID value) {
        this.value = Objects.requireNonNull(value, "PassId cannot be null");
    }

    public static PassId generate() {
        return new PassId(UUID.randomUUID());
    }

    public static PassId fromString(String value) {
        return new PassId(UUID.fromString(value));
    }

    public String getValue() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PassId passId = (PassId) o;
        return Objects.equals(value, passId.value);
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

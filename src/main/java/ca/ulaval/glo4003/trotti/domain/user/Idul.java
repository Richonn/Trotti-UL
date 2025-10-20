package ca.ulaval.glo4003.trotti.domain.user;

import java.util.Objects;

public class Idul {
    private final String value;

    public Idul(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("IDUL cannot be empty");
        }
        this.value = value.toLowerCase();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Idul idul = (Idul) o;
        return Objects.equals(value, idul.value);
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

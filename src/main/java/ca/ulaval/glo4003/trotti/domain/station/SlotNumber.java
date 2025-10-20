package ca.ulaval.glo4003.trotti.domain.station;

import java.util.Objects;

public class SlotNumber {
    private final int value;

    public SlotNumber(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Slot number cannot be negative");
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlotNumber that = (SlotNumber) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

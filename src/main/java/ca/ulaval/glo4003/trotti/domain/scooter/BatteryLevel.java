package ca.ulaval.glo4003.trotti.domain.scooter;

import java.util.Objects;

public class BatteryLevel {
    private static final double MIN_BATTERY = 0.0;
    private static final double MAX_BATTERY = 100.0;
    private static final double LOW_BATTERY_THRESHOLD = 15.0;

    private final double percentage;

    public BatteryLevel(double percentage) {
        if (percentage < MIN_BATTERY || percentage > MAX_BATTERY) {
            throw new IllegalArgumentException("Battery level must be between 0 and 100");
        }
        this.percentage = percentage;
    }

    public static BatteryLevel forInitialization() {
        double percentage = 20 + (Math.random() * 80);
        return new BatteryLevel(percentage);
    }

    public boolean isLow() {
        return percentage <= LOW_BATTERY_THRESHOLD;
    }

    public boolean isSufficientForRide() {
        return !isLow();
    }

    public double getPercentage() {
        return percentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BatteryLevel that = (BatteryLevel) o;
        return Double.compare(that.percentage, percentage) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(percentage);
    }

    @Override
    public String toString() {
        return String.format("%.1f%%", percentage);
    }
}

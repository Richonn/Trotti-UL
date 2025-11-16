package ca.ulaval.glo4003.trotti.domain.scooter;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Scooter {
    private static final double LOW_BATTERY_THRESHOLD = 15.0;

    private final String id;
    private double batteryLevel;
    private String currentStationId;
    private Integer slotNumber;
    private String rentedByUserId;

    public Scooter(String id, double batteryLevel, String stationId, int slotNumber) {
        this.id = Objects.requireNonNull(id, "Scooter ID cannot be null");
        if (batteryLevel < 0 || batteryLevel > 100) {
            throw new IllegalArgumentException("Battery level must be between 0 and 100");
        }
        this.batteryLevel = batteryLevel;
        this.currentStationId = stationId;
        this.slotNumber = slotNumber;
        this.rentedByUserId = null;
    }

    public static Scooter create(String stationId, int slotNumber) {
        BatteryLevel battery = BatteryLevel.forInitialization();
        return new Scooter(UUID.randomUUID().toString(), battery.getPercentage(), stationId, slotNumber);
    }

    public boolean isAvailable() {
        return rentedByUserId == null && !isLowBattery();
    }

    public boolean isLowBattery() {
        return batteryLevel <= LOW_BATTERY_THRESHOLD;
    }

    public void rentTo(String userId) {
        if (!isAvailable()) {
            throw new IllegalStateException("Scooter is not available for rent");
        }
        this.rentedByUserId = userId;
        this.currentStationId = null;
        this.slotNumber = null;
    }

    public void returnToStation(String stationId, int slotNumber) {
        this.rentedByUserId = null;
        this.currentStationId = stationId;
        this.slotNumber = slotNumber;
    }

    public String getId() {
        return id;
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(double batteryLevel) {
        if (batteryLevel < 0 || batteryLevel > 100) {
            throw new IllegalArgumentException("Battery level must be between 0 and 100");
        }
        this.batteryLevel = batteryLevel;
    }

    public Optional<String> getCurrentStationId() {
        return Optional.ofNullable(currentStationId);
    }

    public Optional<Integer> getSlotNumber() {
        return Optional.ofNullable(slotNumber);
    }

    public Optional<String> getRentedByUserId() {
        return Optional.ofNullable(rentedByUserId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scooter scooter = (Scooter) o;
        return Objects.equals(id, scooter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

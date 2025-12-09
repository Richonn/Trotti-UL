package ca.ulaval.glo4003.trotti.domain.scooter;

import ca.ulaval.glo4003.trotti.domain.scooter.exception.LowBatteryException;

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
    private ScooterState state;

    public Scooter(String id, double batteryLevel, String stationId, int slotNumber) {
        this.id = Objects.requireNonNull(id, "Scooter ID cannot be null");
        if (batteryLevel < 0 || batteryLevel > 100) {
            throw new IllegalArgumentException("Battery level must be between 0 and 100");
        }
        this.batteryLevel = batteryLevel;
        this.currentStationId = stationId;
        this.slotNumber = slotNumber;
        this.rentedByUserId = null;
        this.state = ScooterState.AVAILABLE;
    }

    public static Scooter create(String stationId, int slotNumber) {
        BatteryLevel battery = BatteryLevel.forInitialization();
        return new Scooter(UUID.randomUUID().toString(), battery.getPercentage(), stationId, slotNumber);
    }

    public boolean isAvailable() {
        return rentedByUserId == null && !isLowBattery() && state == ScooterState.AVAILABLE;
    }

    public boolean isLowBattery() {
        return batteryLevel <= LOW_BATTERY_THRESHOLD;
    }

    public void startTransfer() {
        if (state != ScooterState.AVAILABLE) {
            throw new IllegalStateException("Scooter is already in transfer");
        }
        this.state = ScooterState.IN_TRANSFER;
        this.currentStationId = null;
        this.slotNumber = null;
    }

    public void completeTransfer(String stationId, int slotNumber) {
        if (state != ScooterState.IN_TRANSFER) {
            throw new IllegalStateException("Scooter is not in transfer");
        }
        this.state = ScooterState.AVAILABLE;
        this.currentStationId = stationId;
        this.slotNumber = slotNumber;
    }

    public ScooterState getState() {
        return state;
    }

    public void rentTo(String userId) {
        if (isLowBattery()) {
            throw new LowBatteryException(this.batteryLevel);
        }
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
}

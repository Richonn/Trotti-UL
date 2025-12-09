package ca.ulaval.glo4003.trotti.application.ride.dto;

public class RideStartResult {
    private final String rideId;
    private final double batteryLevel;

    public RideStartResult(String rideId, double batteryLevel) {
        this.rideId = rideId;
        this.batteryLevel = batteryLevel;
    }

    public String getRideId() {
        return rideId;
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }
}

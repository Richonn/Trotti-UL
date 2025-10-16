package ca.ulaval.glo4003.trotti.scooter.model;

public class Scooter {
    private double batteryLevel;

    public Scooter(double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
}

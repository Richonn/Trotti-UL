package ca.ulaval.glo4003.trotti.scooter.model;

import java.util.HashMap;

public class Station {
    private String location;
    private String name;
    private int capacity;
    private HashMap<Integer, Scooter> scooters;

    public Station(String location, String name, int capacity) {
        this.location = location;
        this.name = name;
        this.capacity = capacity;

        int numberOfScooters = (int) (capacity * 0.8);
        scooters = new HashMap<>();
        for (int i = 0; i < numberOfScooters; i++) {
            double batteryLevel = Math.random() * 100;
            scooters.put(i, new Scooter(batteryLevel));
        }
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public Scooter takeScooter(int slot) {
        if (!scooters.containsKey(slot)) {
            throw new IllegalArgumentException("Scooter not found at the given slot");
        }
        if (scooters.get(slot).getBatteryLevel() <= 15) {
            throw new IllegalArgumentException("Scooter battery too low: " + scooters.get(slot).getBatteryLevel() + "%");
        }
        return scooters.remove(slot);
    }
}

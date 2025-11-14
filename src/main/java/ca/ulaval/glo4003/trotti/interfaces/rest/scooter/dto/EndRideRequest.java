package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto;

public class EndRideRequest {
    private String station;
    private String slot;

    public EndRideRequest() {}

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }
}

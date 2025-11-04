package ca.ulaval.glo4003.trotti.interfaces.rest.ride.dto;

public class ReturnRideRequest {
    private String station;
    private String slot;

    public ReturnRideRequest() {
    }

    public ReturnRideRequest(String station, String slot) {
        this.station = station;
        this.slot = slot;
    }

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

package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto;

public class StartRideRequest {
    private String code;
    private String station;
    private String slot;

    public StartRideRequest() {}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

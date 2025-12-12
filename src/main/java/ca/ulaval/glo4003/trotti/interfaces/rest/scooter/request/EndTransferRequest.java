package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.request;

public class EndTransferRequest {
    private String scooterId;
    private String station;
    private String slot;

    public EndTransferRequest() {}

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getScooterId() {
        return scooterId;
    }

    public void setScooterId(String scooterId) {
        this.scooterId = scooterId;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }
}

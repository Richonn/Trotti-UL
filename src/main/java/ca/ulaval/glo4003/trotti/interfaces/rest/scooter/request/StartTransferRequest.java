package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.request;

public class StartTransferRequest {
    private String station;
    private String slot;

    public StartTransferRequest() {}

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

package ca.ulaval.glo4003.trotti.domain.station.exception;

public class StationFullException extends RuntimeException {
    public StationFullException(String stationId) {
        super("Station " + stationId + " is full");
    }
}

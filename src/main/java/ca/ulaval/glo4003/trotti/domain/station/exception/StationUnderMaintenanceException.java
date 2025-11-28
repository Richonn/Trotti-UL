package ca.ulaval.glo4003.trotti.domain.station.exception;

public class StationUnderMaintenanceException extends RuntimeException {
    public StationUnderMaintenanceException(String stationId) {
        super("Station " + stationId + " is under maintenance");
    }
}

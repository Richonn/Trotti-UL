package ca.ulaval.glo4003.trotti.domain.station.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class StationNotFoundException extends DomainException {
    public StationNotFoundException() {
        super("Station not found");
    }
}

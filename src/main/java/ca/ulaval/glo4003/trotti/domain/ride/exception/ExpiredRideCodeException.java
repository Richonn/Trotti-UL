package ca.ulaval.glo4003.trotti.domain.ride.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class ExpiredRideCodeException extends DomainException {
    public ExpiredRideCodeException() {
        super("Code is expired");
    }
}

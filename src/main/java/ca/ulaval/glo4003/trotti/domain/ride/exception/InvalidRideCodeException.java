package ca.ulaval.glo4003.trotti.domain.ride.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class InvalidRideCodeException extends DomainException {
    public InvalidRideCodeException() {
        super("Code is invalid");
    }
}

package ca.ulaval.glo4003.trotti.domain.ride.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class NoActiveRideException extends DomainException {
    public NoActiveRideException() {
        super("No active ride found for this user");
    }
}

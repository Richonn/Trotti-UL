package ca.ulaval.glo4003.trotti.domain.ride.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class RideNotFoundException extends DomainException {
    public RideNotFoundException() {
        super("Ride not found");
    }
}

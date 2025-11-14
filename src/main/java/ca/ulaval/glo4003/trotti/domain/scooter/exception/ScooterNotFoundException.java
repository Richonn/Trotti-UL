package ca.ulaval.glo4003.trotti.domain.scooter.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class ScooterNotFoundException extends DomainException {
    public ScooterNotFoundException() {
        super("Scooter not found");
    }
}

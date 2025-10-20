package ca.ulaval.glo4003.trotti.domain.pass.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class NoActivePassException extends DomainException {
    public NoActivePassException() {
        super("No active subscription");
    }
}

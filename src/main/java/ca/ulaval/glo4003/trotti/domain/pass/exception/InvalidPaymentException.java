package ca.ulaval.glo4003.trotti.domain.pass.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class InvalidPaymentException extends DomainException {
    public InvalidPaymentException(String message) {
        super(message);
    }
}

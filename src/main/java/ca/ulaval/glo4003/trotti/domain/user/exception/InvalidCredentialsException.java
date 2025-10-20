package ca.ulaval.glo4003.trotti.domain.user.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException() {
        super("Invalid email or password");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}

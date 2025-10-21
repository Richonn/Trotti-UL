package ca.ulaval.glo4003.trotti.domain.user.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class UserAlreadyExistsException extends DomainException {
    public UserAlreadyExistsException() {
        super("Email already in use");
    }
}

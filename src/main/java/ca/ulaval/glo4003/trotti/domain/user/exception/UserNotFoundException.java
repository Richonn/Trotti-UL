package ca.ulaval.glo4003.trotti.domain.user.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException() {
        super("User not found");
    }
}

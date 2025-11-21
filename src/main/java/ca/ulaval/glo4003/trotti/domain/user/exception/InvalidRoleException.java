package ca.ulaval.glo4003.trotti.domain.user.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class InvalidRoleException extends DomainException {
    public InvalidRoleException() {
        super("Invalid user role");
    }
}

package ca.ulaval.glo4003.trotti.domain.user.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class AccountAlreadyActivatedException extends DomainException {
    public AccountAlreadyActivatedException() {
        super("Account already activated");
    }
}

package ca.ulaval.glo4003.trotti.domain.user.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class AccountNotActivatedException extends DomainException {
    public AccountNotActivatedException() {
        super("Account not activated");
    }
}

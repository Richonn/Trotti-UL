package ca.ulaval.glo4003.trotti.domain.user.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class BalanceEmptyException extends DomainException {
    public BalanceEmptyException() {
        super("The balance is empty");
    }
}

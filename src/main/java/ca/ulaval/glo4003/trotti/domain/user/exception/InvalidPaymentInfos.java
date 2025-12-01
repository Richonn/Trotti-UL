package ca.ulaval.glo4003.trotti.domain.user.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class InvalidPaymentInfos extends DomainException {
    
    public InvalidPaymentInfos() {
        super("Invalid payment informations (has expired)");
    }

}

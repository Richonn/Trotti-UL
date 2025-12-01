package ca.ulaval.glo4003.trotti.domain.user.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class UnpaidInvoiceException extends DomainException {
    public UnpaidInvoiceException() {
        super("Cannot rent scooters. Payment is overdue. Please pay your outstanding balance.");
    }
}

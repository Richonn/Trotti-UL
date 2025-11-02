package ca.ulaval.glo4003.trotti.domain.ride.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class PaymentDeclinedException extends DomainException {
    public PaymentDeclinedException() {
        super("Payment was declined. Please check your payment information.");
    }

    public PaymentDeclinedException(String message) {
        super(message);
    }
}

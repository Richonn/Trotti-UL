package ca.ulaval.glo4003.trotti.domain.pass;

import ca.ulaval.glo4003.trotti.domain.shared.TransactionFailedException;
import ca.ulaval.glo4003.trotti.domain.shared.TransactionService;

public class PaymentService {
    private final TransactionService transactionService;

    public PaymentService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void chargePayment(PaymentInfo paymentInfo, Money amount) throws TransactionFailedException {
        transactionService.executeInTransaction(() -> {
            processPayment(paymentInfo, amount);
            return null;
        });
    }

    private void processPayment(PaymentInfo paymentInfo, Money amount) {
        if (paymentInfo == null) {
            throw new IllegalArgumentException("Payment information is required");
        }
        if (amount == null || amount.getAmount().doubleValue() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
    }
}

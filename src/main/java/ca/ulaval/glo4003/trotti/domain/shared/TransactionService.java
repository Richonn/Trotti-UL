package ca.ulaval.glo4003.trotti.domain.shared;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionService {
    private final Random random;
    private final AtomicInteger transactionCounter;
    private static final int FAILURE_RATE = 10;

    public TransactionService() {
        this(new Random());
    }

    public TransactionService(Random random) {
        this.random = random;
        this.transactionCounter = new AtomicInteger(0);
    }

    public <T> T executeInTransaction(TransactionOperation<T> operation) throws TransactionFailedException {
        int transactionId = transactionCounter.incrementAndGet();

        if (shouldSimulateFailure()) {
            throw new TransactionFailedException("Transaction #" + transactionId + " failed due to payment gateway error");
        }

        try {
            return operation.execute();
        } catch (Exception e) {
            throw new TransactionFailedException("Transaction #" + transactionId + " failed: " + e.getMessage(), e);
        }
    }

    private boolean shouldSimulateFailure() {
        return random.nextInt(FAILURE_RATE) == 0;
    }

    @FunctionalInterface
    public interface TransactionOperation<T> {
        T execute() throws Exception;
    }
}

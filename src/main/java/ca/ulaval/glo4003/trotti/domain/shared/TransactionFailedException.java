package ca.ulaval.glo4003.trotti.domain.shared;

public class TransactionFailedException extends RuntimeException {
    public TransactionFailedException(String message) {
        super(message);
    }

    public TransactionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

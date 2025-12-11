package ca.ulaval.glo4003.trotti.domain.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.*;

class TransactionServiceTest {
    private TransactionService transactionService;
    private TransactionService transactionServiceWithSeed;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService();
        transactionServiceWithSeed = new TransactionService(new Random(12345));
    }

    @Test
    void givenSuccessfulOperation_whenExecutingInTransaction_thenReturnsResult() {
        String result = transactionServiceWithSeed.executeInTransaction(() -> "success");

        assertThat(result).isEqualTo("success");
    }

    @Test
    void givenFailingOperation_whenExecutingInTransaction_thenThrowsTransactionFailedException() {
        assertThatThrownBy(() ->
            transactionService.executeInTransaction(() -> {
                throw new RuntimeException("Operation failed");
            })
        ).isInstanceOf(TransactionFailedException.class)
         .hasMessageContaining("Transaction")
         .hasMessageContaining("failed");
    }

    @Test
    void givenMultipleTransactions_whenExecuting_thenEventuallySimulatesFailure() {
        int attemptCount = 0;
        int failureCount = 0;
        int maxAttempts = 100;

        for (int i = 0; i < maxAttempts; i++) {
            attemptCount++;
            try {
                transactionService.executeInTransaction(() -> "success");
            } catch (TransactionFailedException e) {
                failureCount++;
                assertThat(e.getMessage()).contains("payment gateway error");
            }
        }

        assertThat(failureCount).isGreaterThan(0).describedAs("Should simulate at least one failure in %d attempts", maxAttempts);
        assertThat(attemptCount - failureCount).isGreaterThan(0).describedAs("Should have at least one success");
    }

    @Test
    void givenTransactionWithReturnValue_whenExecuting_thenReturnsCorrectValue() {
        Integer result = transactionServiceWithSeed.executeInTransaction(() -> 42);

        assertThat(result).isEqualTo(42);
    }
}

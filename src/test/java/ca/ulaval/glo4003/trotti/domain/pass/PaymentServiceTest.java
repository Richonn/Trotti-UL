package ca.ulaval.glo4003.trotti.domain.pass;

import ca.ulaval.glo4003.trotti.domain.shared.TransactionFailedException;
import ca.ulaval.glo4003.trotti.domain.shared.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    private PaymentService paymentService;
    private TransactionService transactionService;
    private PaymentInfo paymentInfo;
    private Money amount;

    @BeforeEach
    void setUp() {
        transactionService = mock(TransactionService.class);
        paymentService = new PaymentService(transactionService);
        paymentInfo = new PaymentInfo("4111111111111111", "John Doe", "12/25", "123");
        amount = new Money(BigDecimal.valueOf(45.00), Currency.getInstance("CAD"));
    }

    @Test
    void givenValidPaymentInfo_whenChargingPayment_thenExecutesTransaction() throws Exception {
        when(transactionService.executeInTransaction(any())).thenAnswer(invocation -> {
            TransactionService.TransactionOperation<?> operation = invocation.getArgument(0);
            return operation.execute();
        });

        assertThatCode(() -> paymentService.chargePayment(paymentInfo, amount))
                .doesNotThrowAnyException();

        verify(transactionService, times(1)).executeInTransaction(any());
    }

    @Test
    void givenNullPaymentInfo_whenChargingPayment_thenThrowsException() {
        when(transactionService.executeInTransaction(any())).thenAnswer(invocation -> {
            TransactionService.TransactionOperation<?> operation = invocation.getArgument(0);
            return operation.execute();
        });

        assertThatThrownBy(() -> paymentService.chargePayment(null, amount))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenNullAmount_whenChargingPayment_thenThrowsException() {
        when(transactionService.executeInTransaction(any())).thenAnswer(invocation -> {
            TransactionService.TransactionOperation<?> operation = invocation.getArgument(0);
            return operation.execute();
        });

        assertThatThrownBy(() -> paymentService.chargePayment(paymentInfo, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenZeroAmount_whenChargingPayment_thenThrowsException() {
        when(transactionService.executeInTransaction(any())).thenAnswer(invocation -> {
            TransactionService.TransactionOperation<?> operation = invocation.getArgument(0);
            return operation.execute();
        });

        Money zeroAmount = new Money(BigDecimal.ZERO, Currency.getInstance("CAD"));

        assertThatThrownBy(() -> paymentService.chargePayment(paymentInfo, zeroAmount))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenTransactionFailure_whenChargingPayment_thenThrowsTransactionFailedException() {
        when(transactionService.executeInTransaction(any()))
                .thenThrow(new TransactionFailedException("Payment gateway error"));

        assertThatThrownBy(() -> paymentService.chargePayment(paymentInfo, amount))
                .isInstanceOf(TransactionFailedException.class)
                .hasMessageContaining("Payment gateway error");
    }
}

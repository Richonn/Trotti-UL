package ca.ulaval.glo4003.trotti.domain.ride.exception;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentDeclinedExceptionTest {
    @Test
    void givenDefaultConstructor_whenThrown_thenMessageIsCorrect() {
        PaymentDeclinedException exception = new PaymentDeclinedException();

        assertEquals("Payment was declined. Please check your payment information.",
                exception.getMessage());
    }

    @Test
    void givenCustomMessage_whenThrown_thenMessageIsPropagated() {
        String customMessage = "Custom error message";

        PaymentDeclinedException exception = new PaymentDeclinedException(customMessage);

        assertEquals(customMessage, exception.getMessage());
    }
}

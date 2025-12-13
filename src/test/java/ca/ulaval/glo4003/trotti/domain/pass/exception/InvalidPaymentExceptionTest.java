package ca.ulaval.glo4003.trotti.domain.pass.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidPaymentExceptionTest {

    @Test
    void givenMessage_whenCreated_thenReturnsMessage() {
        InvalidPaymentException exception = new InvalidPaymentException("Payment declined");

        assertThat(exception.getMessage()).isEqualTo("Payment declined");
    }

    @Test
    void givenDifferentMessage_whenCreated_thenReturnsDifferentMessage() {
        InvalidPaymentException exception = new InvalidPaymentException("Card expired");

        assertThat(exception.getMessage()).isEqualTo("Card expired");
    }
}

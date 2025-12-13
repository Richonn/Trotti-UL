package ca.ulaval.glo4003.trotti.interfaces.rest.user.response;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentResponseTest {

    @Test
    void givenValidValues_whenCreated_thenReturnsCorrectValues() {
        InvoiceResponse invoice = new InvoiceResponse(
                new BigDecimal("100.00"),
                true,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31)
        );
        PaymentResponse response = new PaymentResponse("Payment successful", invoice);

        assertThat(response.getMessage()).isEqualTo("Payment successful");
        assertThat(response.getInvoice()).isEqualTo(invoice);
    }

    @Test
    void givenDifferentMessage_whenCreated_thenReturnsMessage() {
        InvoiceResponse invoice = new InvoiceResponse(
                new BigDecimal("50.00"),
                false,
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 2, 28)
        );
        PaymentResponse response = new PaymentResponse("Balance paid", invoice);

        assertThat(response.getMessage()).isEqualTo("Balance paid");
    }
}

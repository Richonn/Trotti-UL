package ca.ulaval.glo4003.trotti.interfaces.rest.pass.response;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseResponseTest {

    @Test
    void givenValidValues_whenCreated_thenReturnsCorrectValues() {
        InvoiceResponse invoice = new InvoiceResponse(
                "A2025",
                new BigDecimal("150.00"),
                true,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 4, 30)
        );
        PurchaseResponse response = new PurchaseResponse("Purchase successful", invoice);

        assertThat(response.getMessage()).isEqualTo("Purchase successful");
        assertThat(response.getInvoice()).isEqualTo(invoice);
    }

    @Test
    void givenDifferentMessage_whenCreated_thenReturnsMessage() {
        InvoiceResponse invoice = new InvoiceResponse(
                "H2025",
                new BigDecimal("100.00"),
                false,
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 8, 31)
        );
        PurchaseResponse response = new PurchaseResponse("Pass purchased", invoice);

        assertThat(response.getMessage()).isEqualTo("Pass purchased");
    }
}

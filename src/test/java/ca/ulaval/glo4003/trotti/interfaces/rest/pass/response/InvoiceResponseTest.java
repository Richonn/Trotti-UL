package ca.ulaval.glo4003.trotti.interfaces.rest.pass.response;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class InvoiceResponseTest {

    @Test
    void givenValidValues_whenCreated_thenReturnsCorrectValues() {
        InvoiceResponse response = new InvoiceResponse(
                "A2025",
                new BigDecimal("150.00"),
                true,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 4, 30)
        );

        assertThat(response.getSession()).isEqualTo("A2025");
        assertThat(response.getTotalPrice()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(response.isMonthlyBilling()).isTrue();
    }

    @Test
    void givenNonMonthlyBilling_whenCreated_thenReturnsFalse() {
        InvoiceResponse response = new InvoiceResponse(
                "H2025",
                new BigDecimal("200.00"),
                false,
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 8, 31)
        );

        assertThat(response.isMonthlyBilling()).isFalse();
    }

    @Test
    void givenZeroPrice_whenCreated_thenReturnsZero() {
        InvoiceResponse response = new InvoiceResponse(
                "A2025",
                BigDecimal.ZERO,
                true,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 4, 30)
        );

        assertThat(response.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}

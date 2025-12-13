package ca.ulaval.glo4003.trotti.application.pass.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PassPurchaseResultTest {

    @Test
    void givenValidValues_whenCreated_thenReturnsCorrectValues() {
        PassPurchaseResult result = new PassPurchaseResult(
                "pass-123",
                "A2025",
                new BigDecimal("150.00"),
                true,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 4, 30)
        );

        assertThat(result.getPassId()).isEqualTo("pass-123");
        assertThat(result.getSessionCode()).isEqualTo("A2025");
        assertThat(result.getTotalPrice()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(result.isMonthlyBilling()).isTrue();
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2025, 4, 30));
    }

    @Test
    void givenNonMonthlyBilling_whenCreated_thenReturnsFalse() {
        PassPurchaseResult result = new PassPurchaseResult(
                "pass-456",
                "H2025",
                new BigDecimal("100.00"),
                false,
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 8, 31)
        );

        assertThat(result.isMonthlyBilling()).isFalse();
    }
}

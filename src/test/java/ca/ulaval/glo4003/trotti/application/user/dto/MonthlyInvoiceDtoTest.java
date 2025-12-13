package ca.ulaval.glo4003.trotti.application.user.dto;

import ca.ulaval.glo4003.trotti.domain.pass.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MonthlyInvoiceDtoTest {

    @Test
    void givenValidValues_whenCreated_thenReturnsCorrectValues() {
        Money amount = new Money(new BigDecimal("100.00"));
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);

        MonthlyInvoiceDto dto = new MonthlyInvoiceDto(amount, start, end);

        assertThat(dto.getAmount()).isEqualTo(amount);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
    }

    @Test
    void givenDifferentDates_whenCreated_thenReturnsCorrectDates() {
        Money amount = new Money(new BigDecimal("50.00"));
        LocalDate start = LocalDate.of(2025, 2, 1);
        LocalDate end = LocalDate.of(2025, 2, 28);

        MonthlyInvoiceDto dto = new MonthlyInvoiceDto(amount, start, end);

        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
    }
}

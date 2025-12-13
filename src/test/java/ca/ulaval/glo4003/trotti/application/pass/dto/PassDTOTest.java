package ca.ulaval.glo4003.trotti.application.pass.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PassDTOTest {

    @Test
    void givenValidValues_whenCreated_thenReturnsCorrectValues() {
        PassDTO dto = new PassDTO(
                "pass-123",
                "A2025",
                10,
                true,
                new BigDecimal("150.00"),
                true
        );

        assertThat(dto.getId()).isEqualTo("pass-123");
        assertThat(dto.getSession()).isEqualTo("A2025");
        assertThat(dto.getExtraMinutesPerDay()).isEqualTo(10);
        assertThat(dto.isMonthlyBilling()).isTrue();
        assertThat(dto.getTotalPrice()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(dto.isActive()).isTrue();
    }

    @Test
    void givenInactivePass_whenCreated_thenReturnsInactive() {
        PassDTO dto = new PassDTO(
                "pass-456",
                "H2025",
                0,
                false,
                new BigDecimal("100.00"),
                false
        );

        assertThat(dto.isActive()).isFalse();
        assertThat(dto.isMonthlyBilling()).isFalse();
    }
}

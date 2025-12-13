package ca.ulaval.glo4003.trotti.interfaces.rest.pass.response;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PassResponseTest {

    @Test
    void givenValidValues_whenCreated_thenReturnsCorrectValues() {
        PassResponse response = new PassResponse(
                "pass-123",
                "A2025",
                10,
                true,
                new BigDecimal("150.00"),
                true
        );

        assertThat(response.getId()).isEqualTo("pass-123");
        assertThat(response.getSession()).isEqualTo("A2025");
        assertThat(response.getExtraMinutesPerDay()).isEqualTo(10);
        assertThat(response.isMonthlyBilling()).isTrue();
        assertThat(response.getTotalPrice()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(response.isActive()).isTrue();
    }

    @Test
    void givenInactivePass_whenCreated_thenReturnsInactive() {
        PassResponse response = new PassResponse(
                "pass-456",
                "H2025",
                0,
                false,
                new BigDecimal("100.00"),
                false
        );

        assertThat(response.isActive()).isFalse();
        assertThat(response.isMonthlyBilling()).isFalse();
    }

    @Test
    void givenSetters_whenUsed_thenValuesChange() {
        PassResponse response = new PassResponse();
        response.setId("pass-789");
        response.setSession("A2026");
        response.setExtraMinutesPerDay(20);
        response.setMonthlyBilling(true);
        response.setTotalPrice(new BigDecimal("200.00"));
        response.setActive(true);

        assertThat(response.getId()).isEqualTo("pass-789");
        assertThat(response.getSession()).isEqualTo("A2026");
        assertThat(response.getExtraMinutesPerDay()).isEqualTo(20);
    }
}

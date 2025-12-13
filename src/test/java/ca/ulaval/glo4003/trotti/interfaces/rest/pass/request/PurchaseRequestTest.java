package ca.ulaval.glo4003.trotti.interfaces.rest.pass.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseRequestTest {

    @Test
    void givenValidValues_whenSet_thenReturnsCorrectValues() {
        PurchaseRequest request = new PurchaseRequest();
        request.setSession("A2025");
        request.setExtraMinutesPerDay(10);
        request.setMonthlyBilling(true);
        request.setCardNumber("4111111111111111");
        request.setCardHolder("John Doe");
        request.setExpiryDate("12/2030");
        request.setCvv("123");

        assertThat(request.getSession()).isEqualTo("A2025");
        assertThat(request.getExtraMinutesPerDay()).isEqualTo(10);
        assertThat(request.isMonthlyBilling()).isTrue();
        assertThat(request.getCardNumber()).isEqualTo("4111111111111111");
        assertThat(request.getCardHolder()).isEqualTo("John Doe");
        assertThat(request.getExpiryDate()).isEqualTo("12/2030");
        assertThat(request.getCvv()).isEqualTo("123");
    }

    @Test
    void givenNonMonthlyBilling_whenSet_thenReturnsFalse() {
        PurchaseRequest request = new PurchaseRequest();
        request.setMonthlyBilling(false);

        assertThat(request.isMonthlyBilling()).isFalse();
    }

    @Test
    void givenZeroExtraMinutes_whenSet_thenReturnsZero() {
        PurchaseRequest request = new PurchaseRequest();
        request.setExtraMinutesPerDay(0);

        assertThat(request.getExtraMinutesPerDay()).isEqualTo(0);
    }
}

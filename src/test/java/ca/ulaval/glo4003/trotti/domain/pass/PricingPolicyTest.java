package ca.ulaval.glo4003.trotti.domain.pass;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PricingPolicyTest {

    private PricingPolicy pricingPolicy;

    @BeforeEach
    void setUp() {
        pricingPolicy = new PricingPolicy();
    }

    @Test
    void givenNoExtraMinutes_whenCalculatingPrice_thenReturnsBasePrice() {
        Money price = pricingPolicy.calculatePrice(Session.A2025, 0);

        assertEquals(Money.cad(45), price);
    }

    @Test
    void given10ExtraMinutes_whenCalculatingPrice_thenAdds8DollarsToBase() {
        Money price = pricingPolicy.calculatePrice(Session.A2025, 10);

        assertEquals(Money.cad(53), price);
    }

    @Test
    void given20ExtraMinutes_whenCalculatingPrice_thenAdds16DollarsToBase() {
        Money price = pricingPolicy.calculatePrice(Session.H2026, 20);

        assertEquals(Money.cad(61), price);
    }

    @Test
    void given50ExtraMinutes_whenCalculatingPrice_thenAdds40DollarsToBase() {
        Money price = pricingPolicy.calculatePrice(Session.E2026, 50);

        assertEquals(Money.cad(85), price);
    }

    @Test
    void givenNegativeExtraMinutes_whenCalculatingPrice_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            pricingPolicy.calculatePrice(Session.A2025, -10)
        );
    }

    @Test
    void givenExtraMinutesNotDivisibleBy10_whenCalculatingPrice_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            pricingPolicy.calculatePrice(Session.A2025, 15)
        );
        assertThrows(IllegalArgumentException.class, () ->
            pricingPolicy.calculatePrice(Session.A2025, 7)
        );
        assertThrows(IllegalArgumentException.class, () ->
            pricingPolicy.calculatePrice(Session.A2025, 23)
        );
    }

    @Test
    void givenDifferentSessions_whenCalculatingPrice_thenReturnsConsistentPricing() {
        Money priceA2025 = pricingPolicy.calculatePrice(Session.A2025, 20);
        Money priceH2026 = pricingPolicy.calculatePrice(Session.H2026, 20);
        Money priceE2026 = pricingPolicy.calculatePrice(Session.E2026, 20);

        assertEquals(priceA2025, priceH2026);
        assertEquals(priceA2025, priceE2026);
    }
}

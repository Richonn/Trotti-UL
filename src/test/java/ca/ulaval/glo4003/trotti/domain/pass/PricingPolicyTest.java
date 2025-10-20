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
        // Act
        BigDecimal price = pricingPolicy.calculatePrice(Session.A2025, 0);

        // Assert
        assertEquals(BigDecimal.valueOf(45), price);
    }

    @Test
    void given10ExtraMinutes_whenCalculatingPrice_thenAdds8DollarsToBase() {
        // Act - 1 block of 10 minutes * 2$ * 4 months = 8$
        BigDecimal price = pricingPolicy.calculatePrice(Session.A2025, 10);

        // Assert
        assertEquals(BigDecimal.valueOf(53), price); // 45 + 8
    }

    @Test
    void given20ExtraMinutes_whenCalculatingPrice_thenAdds16DollarsToBase() {
        // Act - 2 blocks of 10 minutes * 2$ * 4 months = 16$
        BigDecimal price = pricingPolicy.calculatePrice(Session.H2026, 20);

        // Assert
        assertEquals(BigDecimal.valueOf(61), price); // 45 + 16
    }

    @Test
    void given50ExtraMinutes_whenCalculatingPrice_thenAdds40DollarsToBase() {
        // Act - 5 blocks of 10 minutes * 2$ * 4 months = 40$
        BigDecimal price = pricingPolicy.calculatePrice(Session.E2026, 50);

        // Assert
        assertEquals(BigDecimal.valueOf(85), price); // 45 + 40
    }

    @Test
    void givenNegativeExtraMinutes_whenCalculatingPrice_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            pricingPolicy.calculatePrice(Session.A2025, -10)
        );
    }

    @Test
    void givenExtraMinutesNotDivisibleBy10_whenCalculatingPrice_thenThrowsException() {
        // Act & Assert
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
        // Act
        BigDecimal priceA2025 = pricingPolicy.calculatePrice(Session.A2025, 20);
        BigDecimal priceH2026 = pricingPolicy.calculatePrice(Session.H2026, 20);
        BigDecimal priceE2026 = pricingPolicy.calculatePrice(Session.E2026, 20);

        // Assert - All sessions should have the same pricing for same extra minutes
        assertEquals(priceA2025, priceH2026);
        assertEquals(priceA2025, priceE2026);
    }
}

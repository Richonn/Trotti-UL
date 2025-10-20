package ca.ulaval.glo4003.trotti.domain.pass;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PassTest {

    private PricingPolicy pricingPolicy;
    private String userId;

    @BeforeEach
    void setUp() {
        pricingPolicy = new PricingPolicy();
        userId = "user-123";
    }

    @Test
    void givenValidData_whenPurchasingPass_thenSucceeds() {
        Pass pass = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);

        assertNotNull(pass.getId());
        assertEquals(userId, pass.getUserId());
        assertEquals(Session.A2025, pass.getSession());
        assertEquals(0, pass.getExtraMinutesPerDay());
        assertFalse(pass.isMonthlyBilling());
        assertEquals(BigDecimal.valueOf(45), pass.getTotalPrice());
        assertTrue(pass.isActive());
    }

    @Test
    void givenPassWithExtraMinutes_whenPurchasing_thenCalculatesPriceCorrectly() {
        Pass pass = Pass.purchase(userId, Session.A2025, 20, true, pricingPolicy);

        assertEquals(20, pass.getExtraMinutesPerDay());
        assertTrue(pass.isMonthlyBilling());
        assertEquals(BigDecimal.valueOf(61), pass.getTotalPrice());
    }

    @Test
    void givenActivePass_whenCheckingIfActiveOnDateInSession_thenReturnsTrue() {
        Pass pass = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);
        LocalDate dateInSession = LocalDate.of(2025, 10, 15);

        assertTrue(pass.isActiveOn(dateInSession));
    }

    @Test
    void givenActivePass_whenCheckingIfActiveOnDateOutsideSession_thenReturnsFalse() {
        Pass pass = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);
        LocalDate dateOutsideSession = LocalDate.of(2026, 2, 15);

        assertFalse(pass.isActiveOn(dateOutsideSession));
    }

    @Test
    void givenDeactivatedPass_whenCheckingIfActiveOnDateInSession_thenReturnsFalse() {
        Pass pass = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);
        LocalDate dateInSession = LocalDate.of(2025, 10, 15);
        pass.deactivate();

        assertFalse(pass.isActiveOn(dateInSession));
    }

    @Test
    void givenActivePass_whenDeactivating_thenBecomesInactive() {
        Pass pass = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);

        pass.deactivate();

        assertFalse(pass.isActive());
    }

    @Test
    void givenTwoPassesWithDifferentIds_whenComparingEquality_thenAreNotEqual() {
        Pass pass1 = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);
        Pass pass2 = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);

        assertNotEquals(pass1, pass2);
    }
}

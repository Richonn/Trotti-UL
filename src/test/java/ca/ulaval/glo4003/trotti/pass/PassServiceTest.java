package ca.ulaval.glo4003.trotti.pass;

import ca.ulaval.glo4003.trotti.pass.dto.PurchaseRequest;
import ca.ulaval.glo4003.trotti.pass.dto.PurchaseResponse;
import ca.ulaval.glo4003.trotti.pass.model.Session;
import ca.ulaval.glo4003.trotti.pass.service.PassService;
import ca.ulaval.glo4003.trotti.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PassServiceTest {
    private PassService passService;
    private User testUser;

    @BeforeEach
    void setUp() {
        passService = new PassService();
        testUser = new User();
        testUser.setName("Alexandra Dupont");
        testUser.setDateOfBirth("2000-01-01");
        testUser.setGender("F");
        testUser.setAge(25);
        testUser.setIdul("dupaa12");
        testUser.setEmail("Alice@Email.com");
        testUser.setPassword("Password@123");
    }

    @Test
    void purchase_withCard_shouldReturnResponse() {
        PurchaseRequest req = new PurchaseRequest();
        req.setSession(Session.A2025);
        req.setExtraMinutesPerDay(20);
        req.setMonthlyBilling(true);
        req.setCardNumber("4111111111111111");
        req.setCardHolder("Alice Dupont");
        req.setExpiryDate("12/28");
        req.setCvv("123");

        PurchaseResponse res = passService.purchase(req, testUser);

        assertNotNull(res);
        assertEquals("Purchase successful", res.getMessage());
        assertNotNull(res.getInvoice());
    }

    @Test
    void purchase_withoutCardButUserHasSavedCard_shouldSucceed() {
        PurchaseRequest req = new PurchaseRequest();
        req.setSession(Session.A2025);
        req.setExtraMinutesPerDay(20);
        req.setMonthlyBilling(true);
        req.setCardNumber("4111111111111111");
        req.setCardHolder("Alice Dupont");
        req.setExpiryDate("12/28");
        req.setCvv("123");

        passService.purchase(req, testUser);

        PurchaseRequest req2 = new PurchaseRequest();
        req2.setSession(Session.H2026);
        req2.setExtraMinutesPerDay(20);
        req2.setMonthlyBilling(false);

        PurchaseResponse res2 = passService.purchase(req2, testUser);
        assertEquals("Purchase successful", res2.getMessage());
    }

    @Test
    void purchase_withoutAnyCard_shouldThrow() {
        PurchaseRequest req = new PurchaseRequest();
        req.setSession(Session.A2025);
        req.setExtraMinutesPerDay(10);
        req.setMonthlyBilling(true);

        assertThrows(RuntimeException.class, () -> passService.purchase(req, testUser));
    }
}

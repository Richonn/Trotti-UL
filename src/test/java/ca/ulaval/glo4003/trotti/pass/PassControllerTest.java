package ca.ulaval.glo4003.trotti.pass;

import ca.ulaval.glo4003.trotti.pass.controller.PassController;
import ca.ulaval.glo4003.trotti.pass.dto.PurchaseRequest;
import ca.ulaval.glo4003.trotti.pass.model.Session;
import ca.ulaval.glo4003.trotti.pass.service.PassService;
import ca.ulaval.glo4003.trotti.shared.config.JwtUtil;
import ca.ulaval.glo4003.trotti.user.model.User;
import ca.ulaval.glo4003.trotti.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;

class PassControllerTest {
    private PassController controller;
    private User testUser;
    private UserService mockUserService;

    @BeforeEach
    void setUp() {
        mockUserService = new UserService();
        controller = new PassController();

        testUser = new User();
        testUser.setName("Alexandra Dupont");
        testUser.setDateOfBirth("2000-01-01");
        testUser.setGender("F");
        testUser.setAge(25);
        testUser.setIdul("dupaa12");
        testUser.setEmail("Alice@Email.com");
        testUser.setPassword("Password@123");

        mockUserService.register(testUser);
    }

    @Test
    void purchase_validTokenAndUser_shouldReturnCreated() {
        PurchaseRequest req = new PurchaseRequest();
        req.setSession(Session.A2025);
        req.setExtraMinutesPerDay(20);
        req.setMonthlyBilling(true);
        req.setCardNumber("4111111111111111");
        req.setCardHolder("Alice Dupont");
        req.setExpiryDate("12/28");
        req.setCvv("123");

        String token = JwtUtil.generateToken(testUser.getEmail());

        Response response = controller.purchase("Bearer " + token, req);

        assertEquals(201, response.getStatus());
    }

    @Test
    void purchase_invalidToken_shouldReturnUnauthorized() {
        Response response = controller.purchase("BadToken", null);
        assertEquals(401, response.getStatus());
    }
}

package ca.ulaval.glo4003.trotti.pass;

import ca.ulaval.glo4003.trotti.pass.controller.PassController;
import ca.ulaval.glo4003.trotti.pass.dto.PurchaseRequest;
import ca.ulaval.glo4003.trotti.pass.dto.PurchaseResponse;
import ca.ulaval.glo4003.trotti.pass.model.Session;
import ca.ulaval.glo4003.trotti.pass.service.PassService;
import ca.ulaval.glo4003.trotti.shared.config.JwtUtil;
import ca.ulaval.glo4003.trotti.user.model.User;
import ca.ulaval.glo4003.trotti.user.service.UserService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PassControllerTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    private PassService passService;
    private UserService userService;
    private PassController controller;
    private User testUser;

    @BeforeEach
    void setUp() {
        passService = mock(PassService.class);
        userService = mock(UserService.class);

        controller = new PassController(passService, userService) {};

        testUser = new User();
        testUser.setEmail("alice@email.com");
    }

    @Test
    void purchase_shouldReturn401_whenTokenInvalid() {
        Response response = controller.purchase("BadToken", new PurchaseRequest());
        assertEquals(401, response.getStatus());
    }

    @Test
    void purchase_shouldReturn401_whenUserNotFound() {
        String token = JwtUtil.generateToken("unknown@email.com");
        when(userService.findByEmail(anyString())).thenReturn(null);

        PurchaseRequest req = new PurchaseRequest();
        req.setSession(Session.A2025);

        Response response = controller.purchase("Bearer " + token, req);
        assertEquals(401, response.getStatus());
    }

    @Test
    void purchase_shouldReturn201_whenValid() {
        String token = JwtUtil.generateToken(testUser.getEmail());
        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(passService.purchase(any(), eq(testUser)))
                .thenReturn(new PurchaseResponse("ok", null));

        PurchaseRequest req = new PurchaseRequest();
        req.setSession(Session.A2025);

        Response response = controller.purchase("Bearer " + token, req);
        assertEquals(201, response.getStatus());
    }

    @Test
    void purchase_shouldReturn400_whenExceptionThrown() {
        String token = JwtUtil.generateToken(testUser.getEmail());
        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(passService.purchase(any(), any())).thenThrow(new RuntimeException("boom"));

        Response response = controller.purchase("Bearer " + token, new PurchaseRequest());
        assertEquals(400, response.getStatus());
    }

    @Test
    void getUserPurchases_shouldReturn401_whenUserNotFound() {
        String token = JwtUtil.generateToken("ghost@email.com");
        when(userService.findByEmail(anyString())).thenReturn(null);

        Response response = controller.getUserPurchases("Bearer " + token);
        assertEquals(401, response.getStatus());
    }

    @Test
    void getUserPurchases_shouldReturn400_whenExceptionThrown() {
        String token = JwtUtil.generateToken(testUser.getEmail());
        when(userService.findByEmail(anyString())).thenThrow(new RuntimeException("fail"));

        Response response = controller.getUserPurchases("Bearer " + token);
        assertEquals(400, response.getStatus());
    }

    @Test
    void getUserPurchases_shouldReturn401_whenTokenInvalid() {
        Response response = controller.getUserPurchases("InvalidToken");
        assertEquals(401, response.getStatus());
    }

    @Test
    void getUserPurchases_shouldReturn200_whenValidUser() {
        User mockUser = mock(User.class);
        when(mockUser.getEmail()).thenReturn(testUser.getEmail());
        when(mockUser.getPurchasedPasses()).thenReturn(java.util.List.of());

        String token = JwtUtil.generateToken(testUser.getEmail());
        when(userService.findByEmail(testUser.getEmail())).thenReturn(mockUser);

        Response response = controller.getUserPurchases("Bearer " + token);

        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
    }
}

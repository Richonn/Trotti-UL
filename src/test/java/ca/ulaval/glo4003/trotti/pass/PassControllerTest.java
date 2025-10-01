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
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PassControllerTest {

    private PassService passService;
    private UserService userService;
    private PassController controller;
    private User testUser;

    @BeforeEach
    void setUp() {
        passService = mock(PassService.class);
        userService = mock(UserService.class);

        controller = new PassController(passService, userService) {
            private final PassService ps = passService;
            private final UserService us = userService;
//            @Override
//            public Response purchase(String authHeader, PurchaseRequest request) {
//                try {
//                    if (authHeader == null || !authHeader.startsWith("Bearer "))
//                        return Response.status(Response.Status.UNAUTHORIZED).build();
//                    String token = authHeader.substring(7);
//                    String email = JwtUtil.extractEmail(token);
//                    User user = us.findByEmail(email);
//                    if (user == null) return Response.status(Response.Status.UNAUTHORIZED).build();
//                    PurchaseResponse response = ps.purchase(request, user);
//                    return Response.status(Response.Status.CREATED).entity(response).build();
//                } catch (Exception e) {
//                    return Response.status(Response.Status.BAD_REQUEST).build();
//                }
//            }
//            @Override
//            public Response getUserPurchases(String authHeader) {
//                try {
//                    if (authHeader == null || !authHeader.startsWith("Bearer "))
//                        return Response.status(Response.Status.UNAUTHORIZED).build();
//                    String token = authHeader.substring(7);
//                    String email = JwtUtil.extractEmail(token);
//                    User user = us.findByEmail(email);
//                    if (user == null) return Response.status(Response.Status.UNAUTHORIZED).build();
//                    return Response.ok(user.getPurchasedPasses()).build();
//                } catch (Exception e) {
//                    return Response.status(Response.Status.BAD_REQUEST).build();
//                }
//            }
        };

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

//    @Test
//    void getUserPurchases_shouldReturn200_whenValidUser() {
//        String token = JwtUtil.generateToken(testUser.getEmail());
//        testUser.setPurchasedPasses(List.of("pass1", "pass2"));
//        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
//
//        Response response = controller.getUserPurchases("Bearer " + token);
//        assertEquals(200, response.getStatus());
//        assertTrue(response.getEntity().toString().contains("pass1"));
//    }

    @Test
    void getUserPurchases_shouldReturn400_whenExceptionThrown() {
        String token = JwtUtil.generateToken(testUser.getEmail());
        when(userService.findByEmail(anyString())).thenThrow(new RuntimeException("fail"));

        Response response = controller.getUserPurchases("Bearer " + token);
        assertEquals(400, response.getStatus());
    }
}

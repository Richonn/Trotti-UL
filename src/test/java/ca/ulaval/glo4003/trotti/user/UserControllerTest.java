package ca.ulaval.glo4003.trotti.user;

import ca.ulaval.glo4003.trotti.user.controller.UserController;
import ca.ulaval.glo4003.trotti.user.dto.LoginRequest;
import ca.ulaval.glo4003.trotti.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController controller;
    private User testUser;

    @BeforeEach
    void setUp() {
        controller = new UserController();
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
    void register_validUser_shouldReturnCreated() {
        Response response = controller.register(testUser);

        assertEquals(201, response.getStatus());
    }

    @Test
    void login_validCredentials_shouldReturnOk() {
        controller.register(testUser);

        LoginRequest request = new LoginRequest();
        request.setEmail("Alice@Email.com");
        request.setPassword("Password@123");

        Response response = controller.login(request);

        assertEquals(200, response.getStatus());
        assertTrue(response.getEntity().toString().contains("token"));
    }

    @Test
    void login_invalid_shouldReturnUnauthorized() {
        LoginRequest request = new LoginRequest();
        request.setEmail("wrong@email.com");
        request.setPassword("wrong");

        Response response = controller.login(request);

        assertEquals(401, response.getStatus());
    }
}

package ca.ulaval.glo4003.trotti.scooter;

import ca.ulaval.glo4003.trotti.pass.service.PassService;
import ca.ulaval.glo4003.trotti.scooter.controller.RideController;
import ca.ulaval.glo4003.trotti.scooter.model.RideCode;
import ca.ulaval.glo4003.trotti.scooter.service.RideService;
import ca.ulaval.glo4003.trotti.shared.config.JwtUtil;
import ca.ulaval.glo4003.trotti.user.model.User;
import ca.ulaval.glo4003.trotti.user.service.UserService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScooterControllerTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    private static final String TEST_EMAIL = "test@example.com";
    private static final String VALID_TOKEN = "Bearer valid.jwt.token";
    private static final String JWT_TOKEN = "valid.jwt.token";
    private static final String TEST_CODE = "123456";

    private RideController rideController;
    private RideService mockRideService;
    private UserService mockUserService;
    private PassService mockPassService;
    private ca.ulaval.glo4003.trotti.scooter.service.StationsService mockStationsService;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockRideService = mock(RideService.class);
        mockUserService = mock(UserService.class);
        mockPassService = mock(PassService.class);
        mockStationsService = mock(ca.ulaval.glo4003.trotti.scooter.service.StationsService.class);

        rideController = new RideController(mockRideService, mockUserService, mockPassService, mockStationsService);

        testUser = new User();
        testUser.setEmail(TEST_EMAIL);
        testUser.activate();
    }

    @Test
    void requestRideCode_shouldReturn401_whenUserNotFound() {
        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractEmail(JWT_TOKEN)).thenReturn(TEST_EMAIL);
            when(mockUserService.findByEmail(TEST_EMAIL)).thenReturn(null);

            Response response = rideController.requestRideCode(VALID_TOKEN);

            assertEquals(401, response.getStatus());
            assertTrue(response.getEntity().toString().contains("User not found"));
        }
    }

    @Test
    void requestRideCode_shouldReturn403_whenAccountNotActivated() {
        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            User inactiveUser = new User();
            inactiveUser.setEmail(TEST_EMAIL);

            mockedJwtUtil.when(() -> JwtUtil.extractEmail(JWT_TOKEN)).thenReturn(TEST_EMAIL);
            when(mockUserService.findByEmail(TEST_EMAIL)).thenReturn(inactiveUser);

            Response response = rideController.requestRideCode(VALID_TOKEN);

            assertEquals(403, response.getStatus());
            assertTrue(response.getEntity().toString().contains("Account not activated"));
        }
    }

    @Test
    void requestRideCode_shouldReturn403_whenNoActiveSubscription() {
        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractEmail(JWT_TOKEN)).thenReturn(TEST_EMAIL);
            when(mockUserService.findByEmail(TEST_EMAIL)).thenReturn(testUser);
            when(mockPassService.hasActiveSession(testUser)).thenReturn(false);

            Response response = rideController.requestRideCode(VALID_TOKEN);

            assertEquals(403, response.getStatus());
            assertTrue(response.getEntity().toString().contains("No active subscription"));
        }
    }

    @Test
    void requestRideCode_shouldReturn200AndCode_whenAllConditionsMet() {
        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            RideCode expectedRideCode = new RideCode(TEST_CODE, LocalDateTime.now().plusSeconds(60));

            mockedJwtUtil.when(() -> JwtUtil.extractEmail(JWT_TOKEN)).thenReturn(TEST_EMAIL);
            when(mockUserService.findByEmail(TEST_EMAIL)).thenReturn(testUser);
            when(mockPassService.hasActiveSession(testUser)).thenReturn(true);
            when(mockRideService.generateAndSendCode(TEST_EMAIL)).thenReturn(expectedRideCode);

            Response response = rideController.requestRideCode(VALID_TOKEN);

            assertEquals(200, response.getStatus());
            assertNotNull(response.getEntity());

            verify(mockRideService, times(1)).generateAndSendCode(TEST_EMAIL);
        }
    }

    @Test
    void requestRideCode_shouldCallGenerateAndSendCodeWithCorrectEmail() {
        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            RideCode expectedRideCode = new RideCode(TEST_CODE, LocalDateTime.now().plusSeconds(60));

            mockedJwtUtil.when(() -> JwtUtil.extractEmail(JWT_TOKEN)).thenReturn(TEST_EMAIL);
            when(mockUserService.findByEmail(TEST_EMAIL)).thenReturn(testUser);
            when(mockPassService.hasActiveSession(testUser)).thenReturn(true);
            when(mockRideService.generateAndSendCode(TEST_EMAIL)).thenReturn(expectedRideCode);

            rideController.requestRideCode(VALID_TOKEN);

            verify(mockRideService).generateAndSendCode(TEST_EMAIL);
        }
    }
}

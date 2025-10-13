package ca.ulaval.glo4003.trotti.scooter;

import ca.ulaval.glo4003.trotti.scooter.model.RideCode;
import ca.ulaval.glo4003.trotti.scooter.service.RideService;
import ca.ulaval.glo4003.trotti.shared.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScooterServiceTest {
    private EmailService mockEmailService;
    private RideService rideService;

    private final String userEmail = "test@example.com";

    @BeforeEach
    void setup() {
        mockEmailService = mock(EmailService.class);
        rideService = new RideService(mockEmailService);
    }

    @Test
    void generateAndSendCode_shouldGenerateCodeBetween4And6Digits() {
        RideCode rideCode = rideService.generateAndSendCode(userEmail);

        assertNotNull(rideCode);
        assertTrue(rideCode.getCode().length() >= 4);
        assertTrue(rideCode.getCode().length() <= 6);
        assertTrue(rideCode.getCode().matches("\\d+"));
    }

    @Test
    void generateAndSendCode_shouldSetExpirationTimeTo60Seconds() {
        LocalDateTime before = LocalDateTime.now().plusSeconds(59);

        RideCode rideCode = rideService.generateAndSendCode(userEmail);

        LocalDateTime after = LocalDateTime.now().plusSeconds(61);
        assertTrue(rideCode.getExpirationTime().isAfter(before));
        assertTrue(rideCode.getExpirationTime().isBefore(after));
    }

    @Test
    void generateAndSendCode_shouldCallEmailService() {
        rideService.generateAndSendCode(userEmail);

        verify(mockEmailService, times(1)).sendEmail(
                eq(userEmail),
                anyString(),
                anyString()
        );
    }

    @Test
    void generateAndSendCode_shouldSendEmailWithCorrectSubject() {
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);

        rideService.generateAndSendCode(userEmail);

        verify(mockEmailService, times(1)).sendEmail(
                eq(userEmail),
                subjectCaptor.capture(),
                anyString()
        );
        assertEquals("Votre code de déverrouillage Trotti UL", subjectCaptor.getValue());
    }

    @Test
    void generateAndSendCode_shouldSendEmailWithCodeInBody() {
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        RideCode rideCode = rideService.generateAndSendCode(userEmail);

        verify(mockEmailService).sendEmail(
                eq(userEmail),
                anyString(),
                bodyCaptor.capture()
        );

        String emailBody = bodyCaptor.getValue();
        assertTrue(emailBody.contains(rideCode.getCode()));
        assertTrue(emailBody.contains("CODE :"));
        assertTrue(emailBody.contains("Trotti UL"));
    }

    @Test
    void isCodeValid_shouldReturnTrue_whenCodeMatchesAndNotExpired() {
        String code = "1234";
        LocalDateTime futureTime = LocalDateTime.now().plusSeconds(30);
        RideCode rideCode = new RideCode(code, futureTime);

        boolean isValid = rideService.isCodeValid(rideCode, "1234");
        assertTrue(isValid);
    }

    @Test
    void isCodeValid_shouldReturnFalse_whenCodeDoesNotMatch() {
        String code = "1234";
        LocalDateTime futureTime = LocalDateTime.now().plusSeconds(30);
        RideCode rideCode = new RideCode(code, futureTime);

        boolean isValid = rideService.isCodeValid(rideCode, "5678");

        assertFalse(isValid);
    }

    @Test
    void isCodeValid_shouldReturnFalse_whenCodeIsExpired() throws InterruptedException {
        String code = "1234";
        LocalDateTime pastTime = LocalDateTime.now().minusSeconds(1);
        RideCode rideCode = new RideCode(code, pastTime);

        boolean isValid = rideService.isCodeValid(rideCode, "1234");

        assertFalse(isValid);
    }

    @Test
    void generateAndSendCode_shouldNeverSendRealEmail() {
        String userEmail = "test@example.com";

        rideService.generateAndSendCode(userEmail);

        verify(mockEmailService, times(1)).sendEmail(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(mockEmailService);
    }
}

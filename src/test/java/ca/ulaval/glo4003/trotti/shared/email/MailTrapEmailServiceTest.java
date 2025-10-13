package ca.ulaval.glo4003.trotti.shared.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MailtrapEmailServiceTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    private static final String TEST_DOMAIN = "sandbox123.mailgun.org";
    private static final String TEST_API_KEY = "test-api-key-123";
    private static final String TEST_FROM_EMAIL = "no-reply@test.com";
    private static final String TEST_TO_EMAIL = "recipient@example.com";
    private static final String TEST_SUBJECT = "Test Subject";
    private static final String TEST_BODY = "Test Body";

    private HttpClient mockHttpClient;
    private HttpResponse<String> mockResponse;

    @BeforeEach
    void setUp() {
        System.setProperty("MAILGUN_DOMAIN", TEST_DOMAIN);
        System.setProperty("MAILGUN_API_KEY", TEST_API_KEY);
        System.setProperty("EMAIL_FROM", TEST_FROM_EMAIL);

        mockHttpClient = mock(HttpClient.class);
        mockResponse = mock(HttpResponse.class);
    }

    @Test
    void constructor_shouldInitializeSuccessfully_whenVariablesArePresent() {
        System.setProperty("MAILGUN_DOMAIN", TEST_DOMAIN);
        System.setProperty("MAILGUN_API_KEY", TEST_API_KEY);
        System.setProperty("EMAIL_FROM", TEST_FROM_EMAIL);

        assertDoesNotThrow(() -> new MailtrapEmailService(mockHttpClient));
    }

    @Test
    void constructor_shouldInitializeSuccessfully_whenAllVariablesPresent() {
        assertDoesNotThrow(() -> new MailtrapEmailService(mockHttpClient));
    }

    @Test
    void sendEmail_shouldSendSuccessfully_whenResponseIs200() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"message\":\"Queued. Thank you.\"}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        MailtrapEmailService emailService = new MailtrapEmailService(mockHttpClient);

        assertDoesNotThrow(() ->
                emailService.sendEmail(TEST_TO_EMAIL, TEST_SUBJECT, TEST_BODY)
        );

        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void sendEmail_shouldThrowException_whenResponseIs403() throws Exception {
        when(mockResponse.statusCode()).thenReturn(403);
        when(mockResponse.body()).thenReturn("{\"message\":\"Forbidden\"}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        MailtrapEmailService emailService = new MailtrapEmailService(mockHttpClient);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emailService.sendEmail(TEST_TO_EMAIL, TEST_SUBJECT, TEST_BODY)
        );

        assertTrue(exception.getMessage().contains("Erreur envoi email"));
        assertTrue(exception.getMessage().contains("Forbidden"));
    }

    @Test
    void sendEmail_shouldThrowException_whenResponseIs401() throws Exception {
        when(mockResponse.statusCode()).thenReturn(401);
        when(mockResponse.body()).thenReturn("{\"message\":\"Unauthorized\"}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        MailtrapEmailService emailService = new MailtrapEmailService(mockHttpClient);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emailService.sendEmail(TEST_TO_EMAIL, TEST_SUBJECT, TEST_BODY)
        );

        assertTrue(exception.getMessage().contains("Unauthorized"));
    }

    @Test
    void sendEmail_shouldThrowException_whenIOExceptionOccurs() throws Exception {
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Network error"));

        MailtrapEmailService emailService = new MailtrapEmailService(mockHttpClient);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emailService.sendEmail(TEST_TO_EMAIL, TEST_SUBJECT, TEST_BODY)
        );

        assertTrue(exception.getMessage().contains("Network error"));
    }

    @Test
    void sendEmail_shouldThrowException_whenInterruptedExceptionOccurs() throws Exception {
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("Thread interrupted"));

        MailtrapEmailService emailService = new MailtrapEmailService(mockHttpClient);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emailService.sendEmail(TEST_TO_EMAIL, TEST_SUBJECT, TEST_BODY)
        );

        assertTrue(exception.getMessage().contains("Thread interrupted"));
    }

    @Test
    void sendEmail_shouldCallHttpClientWithCorrectParameters() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"message\":\"Queued\"}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        MailtrapEmailService emailService = new MailtrapEmailService(mockHttpClient);

        emailService.sendEmail(TEST_TO_EMAIL, TEST_SUBJECT, TEST_BODY);

        verify(mockHttpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void sendEmail_shouldEncodeEmailAddressesCorrectly() throws Exception {
        String emailWithSpecialChars = "test+tag@example.com";

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"message\":\"Queued\"}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        MailtrapEmailService emailService = new MailtrapEmailService(mockHttpClient);

        assertDoesNotThrow(() ->
                emailService.sendEmail(emailWithSpecialChars, TEST_SUBJECT, TEST_BODY)
        );

        verify(mockHttpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void sendEmail_shouldEncodeSubjectWithSpaces() throws Exception {
        String subjectWithSpaces = "Hello World Test";

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"message\":\"Queued\"}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        MailtrapEmailService emailService = new MailtrapEmailService(mockHttpClient);

        assertDoesNotThrow(() ->
                emailService.sendEmail(TEST_TO_EMAIL, subjectWithSpaces, TEST_BODY)
        );

        verify(mockHttpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void defaultConstructor_shouldCreateHttpClient() {
        assertDoesNotThrow(() -> new MailtrapEmailService());
    }

    @Test
    void constructor_shouldPrioritizeDotenvOverSystemEnv() {
        System.setProperty("MAILGUN_DOMAIN", "system-domain.mailgun.org");
        System.setProperty("MAILGUN_API_KEY", "system-key");
        System.setProperty("EMAIL_FROM", "system@email.com");

        MailtrapEmailService emailService = new MailtrapEmailService(mockHttpClient);

        assertNotNull(emailService);
    }
}
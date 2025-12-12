package ca.ulaval.glo4003.trotti.infrastructure.email;

import ca.ulaval.glo4003.trotti.domain.shared.EmailMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MailgunEmailAdapterTest {

    private EmailConfig config;
    private HttpClient httpClient;
    private HttpResponse<String> httpResponse;
    private MailgunEmailAdapter adapter;

    @BeforeEach
    void setup() {
        config = mock(EmailConfig.class);
        httpClient = mock(HttpClient.class);
        httpResponse = mock(HttpResponse.class);

        when(config.getApiKey()).thenReturn("testkey");
        when(config.getDomain()).thenReturn("example.com");
        when(config.getFromEmail()).thenReturn("sender@example.com");

        adapter = new MailgunEmailAdapter(config, httpClient);
    }

    @Test
    void givenValidResponse_whenSendEmail_thenDoesNotThrow() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);

        assertDoesNotThrow(() ->
                adapter.sendEmail("to@example.com", "Subject", "Body")
        );
    }

    @Test
    void givenErrorStatusCode_whenSendEmail_thenThrowsRuntimeException() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(500);
        when(httpResponse.body()).thenReturn("Internal error");

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adapter.sendEmail("to@example.com", "Subject", "Body")
        );

        assertTrue(exception.getMessage().contains("500"));
    }

    @Test
    void givenIOException_whenSendEmail_thenThrowsRuntimeException() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("IO fail"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adapter.sendEmail("to@example.com", "Subject", "Body")
        );

        assertTrue(exception.getMessage().contains("IO fail"));
    }

    @Test
    void givenInterruptedException_whenSendEmail_thenThrowsRuntimeException() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("Interrupted"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adapter.sendEmail("to@example.com", "Subject", "Body")
        );

        assertTrue(exception.getMessage().contains("Interrupted"));
    }
}
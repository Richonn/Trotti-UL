package ca.ulaval.glo4003.trotti.infrastructure.email;

import ca.ulaval.glo4003.trotti.domain.shared.EmailMessage;
import ca.ulaval.glo4003.trotti.domain.shared.EmailService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MailgunEmailAdapter implements EmailService {

    private final EmailConfig config;
    private final HttpClient httpClient;

    public MailgunEmailAdapter(EmailConfig config) {
        this(config, HttpClient.newHttpClient());
    }

    public MailgunEmailAdapter(EmailConfig config, HttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        String auth = "api:" + config.getApiKey();
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        String formData = String.format(
                "from=%s&to=%s&subject=%s&text=%s",
                urlEncode(config.getFromEmail()),
                urlEncode(to),
                urlEncode(subject),
                urlEncode(body)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mailgun.net/v3/" + config.getDomain() + "/messages"))
                .header("Authorization", "Basic " + encodedAuth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Mailgun Response Status: " + response.statusCode());
            System.out.println("Mailgun Response Body: " + response.body());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Email sending error (status " + response.statusCode() + "): " + response.body());
            }

            System.out.println("Email sent successfully to: " + to);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Email sending error: " + e.getMessage(), e);
        }
    }

    @Override
    public void send(EmailMessage email) {
        sendEmail(email.getRecipient(), email.getSubject(), email.getBody());
    }

    private String urlEncode(String value) {
        try {
            return java.net.URLEncoder.encode(value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }
}

package ca.ulaval.glo4003.trotti.infrastructure.email;

import ca.ulaval.glo4003.trotti.domain.shared.EmailMessage;
import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MailtrapEmailAdapter implements EmailService {

    private final String domain;
    private final String apiKey;
    private final String fromEmail;
    private final HttpClient httpClient;

    public MailtrapEmailAdapter() {
        this(HttpClient.newHttpClient());
    }

    public MailtrapEmailAdapter(HttpClient httpClient) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        this.domain = dotenv.get("MAILGUN_DOMAIN", System.getenv("MAILGUN_DOMAIN"));
        this.apiKey = dotenv.get("MAILGUN_API_KEY", System.getenv("MAILGUN_API_KEY"));
        this.fromEmail = dotenv.get("EMAIL_FROM", System.getenv("EMAIL_FROM"));
        this.httpClient = httpClient;

        if (domain == null || apiKey == null || fromEmail == null) {
            throw new RuntimeException("Variables d'environnement manquantes. Vérifiez MAILGUN_DOMAIN, MAILGUN_API_KEY et EMAIL_FROM");
        }
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        String auth = "api:" + apiKey;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        String formData = String.format(
                "from=%s&to=%s&subject=%s&text=%s",
                urlEncode(fromEmail),
                urlEncode(to),
                urlEncode(subject),
                urlEncode(body)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mailgun.net/v3/" + domain + "/messages"))
                .header("Authorization", "Basic " + encodedAuth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Mailgun Response Status: " + response.statusCode());
            System.out.println("Mailgun Response Body: " + response.body());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erreur envoi email (status " + response.statusCode() + "): " + response.body());
            }

            System.out.println("Email sent successfully to: " + to);
        } catch (IOException | InterruptedException e) {
            System.err.println("Erreur lors de l'envoi d'email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur envoi mail: " + e.getMessage(), e);
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

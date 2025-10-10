package ca.ulaval.glo4003.trotti.shared.email;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MailtrapEmailService implements EmailService {

    private final String domain;
    private final String apiKey;
    private final String fromEmail;

    public MailtrapEmailService() {
        this.domain = System.getenv("MAILGUN_DOMAIN");
        this.apiKey = System.getenv("MAILGUN_API_KEY");
        this.fromEmail = System.getenv("EMAIL_FROM");

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

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mailgun.net/v3/" + domain + "/messages"))
                .header("Authorization", "Basic " + encodedAuth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        try {
            System.out.println("Envoi d'email via Mailgun à " + to);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Email envoyé avec succès ! " + response.body());
            } else {
                System.err.println("Erreur Mailgun: " + response.statusCode() + " - " + response.body());
                throw new RuntimeException("Erreur envoi email: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur envoi mail: " + e.getMessage(), e);
        }
    }

    private String urlEncode(String value) {
        try {
            return java.net.URLEncoder.encode(value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }
}
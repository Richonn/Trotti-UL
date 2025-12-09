package ca.ulaval.glo4003.trotti.infrastructure.email;

import io.github.cdimascio.dotenv.Dotenv;

public class EmailConfig {
    private final String domain;
    private final String apiKey;
    private final String fromEmail;

    private EmailConfig(String domain, String apiKey, String fromEmail) {
        this.domain = domain;
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
    }

    public static EmailConfig fromEnvironment() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        String domain = dotenv.get("MAILGUN_DOMAIN", System.getenv("MAILGUN_DOMAIN"));
        String apiKey = dotenv.get("MAILGUN_API_KEY", System.getenv("MAILGUN_API_KEY"));
        String fromEmail = dotenv.get("EMAIL_FROM", System.getenv("EMAIL_FROM"));

        if (domain == null || apiKey == null || fromEmail == null) {
            throw new IllegalStateException("Missing email configuration: MAILGUN_DOMAIN, MAILGUN_API_KEY, and EMAIL_FROM must be set");
        }

        return new EmailConfig(domain, apiKey, fromEmail);
    }

    public String getDomain() {
        return domain;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getFromEmail() {
        return fromEmail;
    }
}

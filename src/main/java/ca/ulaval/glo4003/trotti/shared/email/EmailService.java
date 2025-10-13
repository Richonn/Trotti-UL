package ca.ulaval.glo4003.trotti.shared.email;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}

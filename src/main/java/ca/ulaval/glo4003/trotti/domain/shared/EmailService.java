package ca.ulaval.glo4003.trotti.domain.shared;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}

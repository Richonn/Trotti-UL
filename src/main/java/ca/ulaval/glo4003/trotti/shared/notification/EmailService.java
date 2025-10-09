package ca.ulaval.glo4003.trotti.shared.notification;

public class EmailService {
    public void send(String email, String subject, String message) {
        System.out.println("EMAIL SENT TO " + email + ": " + subject + " - " + message);
    }
}

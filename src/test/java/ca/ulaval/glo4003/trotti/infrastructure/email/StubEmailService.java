package ca.ulaval.glo4003.trotti.infrastructure.email;

import ca.ulaval.glo4003.trotti.domain.shared.EmailMessage;
import ca.ulaval.glo4003.trotti.domain.shared.EmailService;

import java.util.ArrayList;
import java.util.List;

public class StubEmailService implements EmailService {
    private final List<EmailMessage> sentEmails = new ArrayList<>();

    @Override
    public void sendEmail(String to, String subject, String body) {
    }

    @Override
    public void send(EmailMessage email) {
        sentEmails.add(email);
    }

    public List<EmailMessage> getSentEmails() {
        return new ArrayList<>(sentEmails);
    }

    public void clear() {
        sentEmails.clear();
    }

    public int getCount() {
        return sentEmails.size();
    }

    public boolean hasEmailTo(String recipient) {
        return sentEmails.stream()
                .anyMatch(email -> email.getRecipient().equals(recipient));
    }
}

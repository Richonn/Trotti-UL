package ca.ulaval.glo4003.trotti.domain.shared;

public interface EmailMessage {
    String getRecipient();
    String getSubject();
    String getBody();
}

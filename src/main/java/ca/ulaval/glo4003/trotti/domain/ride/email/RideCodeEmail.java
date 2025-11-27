package ca.ulaval.glo4003.trotti.domain.ride.email;

import ca.ulaval.glo4003.trotti.domain.ride.RideCode;
import ca.ulaval.glo4003.trotti.domain.shared.EmailMessage;

import java.time.format.DateTimeFormatter;

public class RideCodeEmail implements EmailMessage {
    private final String recipientEmail;
    private final String recipientName;
    private final RideCode rideCode;

    public RideCodeEmail(String recipientEmail, String recipientName, RideCode rideCode) {
        this.recipientEmail = recipientEmail;
        this.recipientName = recipientName;
        this.rideCode = rideCode;
    }

    @Override
    public String getRecipient() {
        return recipientEmail;
    }

    @Override
    public String getSubject() {
        return "Votre code de déverrouillage Trotti UL";
    }

    @Override
    public String getBody() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String expirationTimeFormatted = rideCode.getExpirationTime().format(formatter);

        return String.format(
                "Bonjour %s,\n\n" +
                "Voici votre code de déverrouillage pour votre trottinette :\n\n" +
                "CODE : %s\n\n" +
                "Ce code expire à %s (valide 60 secondes)\n\n" +
                "Bon voyage !\n\n" +
                "L'équipe Trotti UL",
                recipientName,
                rideCode.getCode(),
                expirationTimeFormatted
        );
    }
}

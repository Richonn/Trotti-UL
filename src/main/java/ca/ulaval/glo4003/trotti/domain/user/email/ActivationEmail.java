package ca.ulaval.glo4003.trotti.domain.user.email;

import ca.ulaval.glo4003.trotti.domain.shared.EmailMessage;

public class ActivationEmail implements EmailMessage {
    private final String recipientEmail;
    private final String recipientName;

    public ActivationEmail(String recipientEmail, String recipientName) {
        this.recipientEmail = recipientEmail;
        this.recipientName = recipientName;
    }

    @Override
    public String getRecipient() {
        return recipientEmail;
    }

    @Override
    public String getSubject() {
        return "Bienvenue à TrottiUL - Compte activé !";
    }

    @Override
    public String getBody() {
        return "Bonjour " + recipientName + ",\n\n" +
               "Félicitations ! Votre compte TrottiUL a été activé avec succès.\n\n" +
               "Vous pouvez maintenant :\n" +
               "- Acheter un laissez-passer pour la session en cours\n" +
               "- Demander des codes de déverrouillage\n" +
               "- Commencer à louer des trottinettes\n\n" +
               "Cordialement,\n" +
               "L'équipe TrottiUL";
    }
}

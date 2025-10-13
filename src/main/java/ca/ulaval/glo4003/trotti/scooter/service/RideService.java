package ca.ulaval.glo4003.trotti.scooter.service;

import ca.ulaval.glo4003.trotti.scooter.model.RideCode;
import ca.ulaval.glo4003.trotti.shared.email.EmailService;
import ca.ulaval.glo4003.trotti.shared.email.MailtrapEmailService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class RideService {

    private final EmailService emailService;

    public RideService() {
        this.emailService = new MailtrapEmailService();
    }

    public RideService(EmailService emailService) {
        this.emailService = emailService;
    }

    public RideCode generateAndSendCode(String userEmail) {
        String code = generateNumericCode(4, 6);
        LocalDateTime expirationTime = LocalDateTime.now().plusSeconds(60);
        RideCode rideCode = new RideCode(code, expirationTime);

        sendCodeByEmail(userEmail, rideCode);

        return new RideCode(code, expirationTime);
    }

    private void sendCodeByEmail(String email, RideCode rideCode) {
        String subject = "Votre code de déverrouillage Trotti UL";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String expirationTimeFormatted = rideCode.getExpirationTime().format(formatter);

        String body = String.format(
                "Bonjour,\n\n" +
                        "Voici votre code de déverrouillage pour votre trottinette :\n\n" +
                        "CODE : %s\n\n" +
                        "Ce code expire à %s (valide 60 secondes)\n\n" +
                        "Bon voyage !\n\n" +
                        "L'équipe Trotti UL",
                rideCode.getCode(),
                expirationTimeFormatted
        );

        emailService.sendEmail(email, subject, body);
    }

    private String generateNumericCode(int minLength, int maxLength) {
        Random random = new Random();
        int length = random.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    public boolean isCodeValid(RideCode rideCode, String inputCode) {
        return rideCode.getCode().equals(inputCode) && !rideCode.isExpired();
    }
}

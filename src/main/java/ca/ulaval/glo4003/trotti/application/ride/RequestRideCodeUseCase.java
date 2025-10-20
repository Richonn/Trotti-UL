package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.pass.exception.NoActivePassException;
import ca.ulaval.glo4003.trotti.domain.ride.RideCode;
import ca.ulaval.glo4003.trotti.domain.ride.RideCodeGenerator;
import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.AccountNotActivatedException;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestRideCodeUseCase {
    private final UserRepository userRepository;
    private final PassRepository passRepository;
    private final RideCodeGenerator rideCodeGenerator;
    private final EmailService emailService;
    private final Map<String, RideCode> userRideCodes = new ConcurrentHashMap<>();

    public RequestRideCodeUseCase(UserRepository userRepository, PassRepository passRepository,
                                   RideCodeGenerator rideCodeGenerator, EmailService emailService) {
        this.userRepository = userRepository;
        this.passRepository = passRepository;
        this.rideCodeGenerator = rideCodeGenerator;
        this.emailService = emailService;
    }

    public RideCode execute(String userEmail) {
        Email email = new Email(userEmail);
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if (!user.isActivated()) {
            throw new AccountNotActivatedException();
        }

        if (!passRepository.hasActivePassForUser(user.getId(), LocalDate.now())) {
            throw new NoActivePassException();
        }

        RideCode rideCode = rideCodeGenerator.generate();
        userRideCodes.put(userEmail, rideCode);

        sendCodeByEmail(userEmail, rideCode);

        return rideCode;
    }

    public RideCode getUserCode(String userEmail) {
        return userRideCodes.get(userEmail);
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
}

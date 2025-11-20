package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.pass.exception.NoActivePassException;
import ca.ulaval.glo4003.trotti.domain.ride.RideCode;
import ca.ulaval.glo4003.trotti.domain.ride.RideCodeGenerator;
import ca.ulaval.glo4003.trotti.domain.ride.RideCodeRepository;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.AccountNotActivatedException;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;

import java.time.format.DateTimeFormatter;

public class RequestRideCodeUseCase {
    private final UserRepository userRepository;
    private final PassRepository passRepository;
    private final RideCodeGenerator rideCodeGenerator;
    private final RideCodeRepository rideCodeRepository;
    private final EmailService emailService;
    private final Clock clock;

    public RequestRideCodeUseCase(UserRepository userRepository, PassRepository passRepository,
                                   RideCodeGenerator rideCodeGenerator, RideCodeRepository rideCodeRepository,
                                   EmailService emailService, Clock clock) {
        this.userRepository = userRepository;
        this.passRepository = passRepository;
        this.rideCodeGenerator = rideCodeGenerator;
        this.rideCodeRepository = rideCodeRepository;
        this.emailService = emailService;
        this.clock = clock;
    }

    public RideCode execute(String userEmail) {
        Email email = new Email(userEmail);
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if (!user.isActivated()) {
            throw new AccountNotActivatedException();
        }

        if (!user.hasActivePass(passRepository.findByUserId(user.getId()), clock.today())) {
            throw new NoActivePassException();
        }

        RideCode rideCode = rideCodeGenerator.generate();
        rideCodeRepository.save(userEmail, rideCode);

        sendCodeByEmail(userEmail, rideCode);

        return rideCode;
    }

    public RideCode getUserCode(String userEmail) {
        return rideCodeRepository.findByUserEmail(userEmail).orElse(null);
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

package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.ride.RideCode;
import ca.ulaval.glo4003.trotti.domain.ride.RideCodeGenerator;
import ca.ulaval.glo4003.trotti.domain.ride.RideCodeRepository;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;

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

        RideCode rideCode = user.requestRideCode(
                passRepository.findByUserId(user.getId()),
                clock,
                rideCodeGenerator,
                emailService
        );

        rideCodeRepository.save(userEmail, rideCode);

        return rideCode;
    }

    public RideCode getUserCode(String userEmail) {
        return rideCodeRepository.findByUserEmail(userEmail).orElse(null);
    }
}

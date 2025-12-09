package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.domain.ride.RideCode;
import ca.ulaval.glo4003.trotti.domain.ride.RideCodeGenerator;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;

public class RequestRideCodeUseCase {
    private final UserRepository userRepository;
    private final RideCodeGenerator rideCodeGenerator;
    private final EmailService emailService;
    private final Clock clock;

    public RequestRideCodeUseCase(UserRepository userRepository,
                                   RideCodeGenerator rideCodeGenerator,
                                   EmailService emailService, Clock clock) {
        this.userRepository = userRepository;
        this.rideCodeGenerator = rideCodeGenerator;
        this.emailService = emailService;
        this.clock = clock;
    }

    public RideCode execute(String userEmail) {
        Email email = new Email(userEmail);
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        RideCode rideCode = user.requestRideCode(
                clock,
                rideCodeGenerator,
                emailService
        );

        userRepository.save(user);

        return rideCode;
    }

    public RideCode getUserCode(String userEmail) {
        Email email = new Email(userEmail);
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        return user.getCurrentRideCode().orElse(null);
    }
}

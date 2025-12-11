package ca.ulaval.glo4003.trotti.functional;

import ca.ulaval.glo4003.trotti.domain.pass.Pass;
import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.pass.PricingPolicy;
import ca.ulaval.glo4003.trotti.domain.pass.Session;
import ca.ulaval.glo4003.trotti.domain.ride.RideCodeRepository;
import ca.ulaval.glo4003.trotti.domain.security.TokenGenerator;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.Password;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;

public class TestFixtures {
    private final UserRepository userRepository;
    private final PassRepository passRepository;
    private final RideCodeRepository rideCodeRepository;
    private final TokenGenerator tokenGenerator;
    private final EmailService emailService;
    private final Clock clock;

    public TestFixtures(UserRepository userRepository, PassRepository passRepository, RideCodeRepository rideCodeRepository, TokenGenerator tokenGenerator, EmailService emailService, Clock clock) {
        this.userRepository = userRepository;
        this.passRepository = passRepository;
        this.rideCodeRepository = rideCodeRepository;
        this.tokenGenerator = tokenGenerator;
        this.emailService = emailService;
        this.clock = clock;
    }

    public User createUser(String email, String password, String name, String idul) {
        User user = User.register(
            new Email(email),
            new Password(password),
            name,
            "2000-01-01",
            "H",
            idul,
            Role.USER
        );
        userRepository.save(user);
        return user;
    }

    public User createActivatedUser(String email, String password, String name, String idul) {
        User user = createUser(email, password, name, idul);
        user.activate(emailService);
        userRepository.save(user);
        return user;
    }

    public User createActivatedUserWithPass(String email, String password, String name, String idul, Session session) {
        User user = createActivatedUser(email, password, name, idul);

        Pass pass = Pass.purchase(
            user.getId(),
            session,
            0,
            false,
            new PricingPolicy()
        );
        user.addPass(pass);
        userRepository.save(user);

        return user;
    }

    public String generateToken(String email) {
        return tokenGenerator.generateToken(email);
    }

    public void clearAllUsers() {
        userRepository.deleteAll();
        passRepository.deleteAll();
        rideCodeRepository.deleteAll();
    }
}

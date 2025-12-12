package ca.ulaval.glo4003.trotti.interfaces.rest.config;

import ca.ulaval.glo4003.trotti.application.user.*;
import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.ride.RideCodeRepository;
import ca.ulaval.glo4003.trotti.domain.security.TokenGenerator;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.user.UserFactory;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.infrastructure.persistence.memory.InMemoryUserRepository;
import ca.ulaval.glo4003.trotti.infrastructure.time.SystemClock;

public class UserContext {
    private final UserRepository userRepository;
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final ActivateAccountUseCase activateAccountUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUserBalanceUseCase getUserBalanceUseCase;


    public UserContext(EmailService emailService, TokenGenerator tokenGenerator, Clock clock,
                       PassRepository passRepository, RideCodeRepository rideCodeRepository) {
        UserFactory userFactory = new UserFactory();
        this.userRepository = new InMemoryUserRepository(passRepository, rideCodeRepository);

        this.registerUserUseCase = new RegisterUserUseCase(userRepository, emailService, clock, userFactory);
        this.loginUserUseCase = new LoginUserUseCase(userRepository, tokenGenerator);
        this.activateAccountUseCase = new ActivateAccountUseCase(userRepository, emailService);
        this.getAllUsersUseCase = new GetAllUsersUseCase(userRepository, clock);
        this.getUserBalanceUseCase = new GetUserBalanceUseCase(userRepository);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public RegisterUserUseCase getRegisterUserUseCase() {
        return registerUserUseCase;
    }

    public LoginUserUseCase getLoginUserUseCase() {
        return loginUserUseCase;
    }

    public ActivateAccountUseCase getActivateAccountUseCase() {
        return activateAccountUseCase;
    }

    public GetAllUsersUseCase getGetAllUsersUseCase() {
        return getAllUsersUseCase;
    }

    public GetUserBalanceUseCase getGetUserBalanceUseCase() {
        return getUserBalanceUseCase;
    }
}

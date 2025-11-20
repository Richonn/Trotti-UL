package ca.ulaval.glo4003.trotti.interfaces.rest.config;

import ca.ulaval.glo4003.trotti.application.pass.GetUserPassesUseCase;
import ca.ulaval.glo4003.trotti.application.pass.PurchasePassUseCase;
import ca.ulaval.glo4003.trotti.application.ride.EndRideUseCase;
import ca.ulaval.glo4003.trotti.application.ride.GetRideHistoryUseCase;
import ca.ulaval.glo4003.trotti.application.ride.RequestRideCodeUseCase;
import ca.ulaval.glo4003.trotti.application.ride.StartRideUseCase;
import ca.ulaval.glo4003.trotti.application.scooter.EndTransferScooterUseCase;
import ca.ulaval.glo4003.trotti.application.scooter.StartTransferScooterUseCase;
import ca.ulaval.glo4003.trotti.application.station.EndStationMaintenanceUseCase;
import ca.ulaval.glo4003.trotti.application.station.RequestStationMaintenanceUseCase;
import ca.ulaval.glo4003.trotti.application.station.StartStationMaintenanceUseCase;
import ca.ulaval.glo4003.trotti.application.user.ActivateAccountUseCase;
import ca.ulaval.glo4003.trotti.application.user.GetAllUsersUseCase;
import ca.ulaval.glo4003.trotti.application.user.LoginUserUseCase;
import ca.ulaval.glo4003.trotti.application.user.RegisterUserUseCase;
import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.pass.PricingPolicy;
import ca.ulaval.glo4003.trotti.domain.ride.DailyUsageRepository;
import ca.ulaval.glo4003.trotti.domain.ride.RideCodeGenerator;
import ca.ulaval.glo4003.trotti.domain.ride.RideCodeRepository;
import ca.ulaval.glo4003.trotti.domain.ride.RideFeeCalculator;
import ca.ulaval.glo4003.trotti.domain.ride.RideRepository;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.security.TokenGenerator;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.station.Station;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.Password;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.infrastructure.email.MailtrapEmailAdapter;
import ca.ulaval.glo4003.trotti.infrastructure.persistence.memory.*;
import ca.ulaval.glo4003.trotti.infrastructure.security.JwtTokenService;
import ca.ulaval.glo4003.trotti.infrastructure.time.SystemClock;

public class ApplicationContext {
    private static ApplicationContext instance;

    private final UserRepository userRepository;
    private final PassRepository passRepository;
    private final StationRepository stationRepository;
    private final ScooterRepository scooterRepository;
    private final RideRepository rideRepository;
    private final RideCodeRepository rideCodeRepository;

    private final EmailService emailService;
    private final TokenGenerator tokenGenerator;
    private final Clock clock;

    private final PricingPolicy pricingPolicy;
    private final RideCodeGenerator rideCodeGenerator;
    private final RideFeeCalculator rideFeeCalculator;
    private final DailyUsageRepository dailyUsageRepository;

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final ActivateAccountUseCase activateAccountUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;

    private final PurchasePassUseCase purchasePassUseCase;
    private final GetUserPassesUseCase getUserPassesUseCase;

    private final RequestRideCodeUseCase requestRideCodeUseCase;
    private final StartRideUseCase startRideUseCase;
    private final EndRideUseCase endRideUseCase;
    private final GetRideHistoryUseCase getRideHistoryUseCase;

    private final StartStationMaintenanceUseCase startStationMaintenanceUseCase;
    private final EndStationMaintenanceUseCase endStationMaintenanceUseCase;
    private final RequestStationMaintenanceUseCase requestStationMaintenanceUseCase;

    private final StartTransferScooterUseCase startTransferScooterUseCase;
    private final EndTransferScooterUseCase endTransferScooterUseCase;

    private ApplicationContext() {
        this.userRepository = new InMemoryUserRepository();
        this.passRepository = new InMemoryPassRepository();
        this.stationRepository = new InMemoryStationRepository();
        this.scooterRepository = new InMemoryScooterRepository();
        this.rideRepository = new InMemoryRideRepository();
        this.rideCodeRepository = new InMemoryRideCodeRepository();

        this.emailService = new MailtrapEmailAdapter();
        this.tokenGenerator = new JwtTokenService();
        this.clock = new SystemClock();

        this.pricingPolicy = new PricingPolicy();
        this.rideCodeGenerator = new RideCodeGenerator(clock);
        this.rideFeeCalculator = new RideFeeCalculator();
        this.dailyUsageRepository = new InMemoryDailyUsageRepository();

        this.registerUserUseCase = new RegisterUserUseCase(userRepository, emailService);
        this.loginUserUseCase = new LoginUserUseCase(userRepository, tokenGenerator);
        this.activateAccountUseCase = new ActivateAccountUseCase(userRepository, emailService);
        this.getAllUsersUseCase = new GetAllUsersUseCase(userRepository);
        this.purchasePassUseCase = new PurchasePassUseCase(userRepository, passRepository, pricingPolicy, clock);
        this.getUserPassesUseCase = new GetUserPassesUseCase(userRepository, passRepository);
        this.startStationMaintenanceUseCase = new StartStationMaintenanceUseCase(stationRepository);
        this.endStationMaintenanceUseCase = new EndStationMaintenanceUseCase(stationRepository);
        this.requestStationMaintenanceUseCase = new RequestStationMaintenanceUseCase(userRepository, stationRepository, emailService);
        this.startTransferScooterUseCase = new StartTransferScooterUseCase(stationRepository, scooterRepository);
        this.endTransferScooterUseCase = new EndTransferScooterUseCase(stationRepository, scooterRepository);
        this.requestRideCodeUseCase = new RequestRideCodeUseCase(userRepository, passRepository, rideCodeGenerator, rideCodeRepository, emailService, clock);
        this.startRideUseCase = new StartRideUseCase(userRepository, passRepository, stationRepository, scooterRepository, rideRepository, requestRideCodeUseCase, clock);
        this.endRideUseCase = new EndRideUseCase(userRepository, rideRepository, scooterRepository, stationRepository, passRepository, rideFeeCalculator, dailyUsageRepository, clock);
        this.getRideHistoryUseCase = new GetRideHistoryUseCase(userRepository, rideRepository);

        initializeData();
    }

    private void initializeData() {
        stationRepository.initialize();

        InMemoryScooterRepository scooterRepo = (InMemoryScooterRepository) scooterRepository;
        for (Station station : stationRepository.findAll()) {
            scooterRepo.initializeScootersForStation(station.getId(), station.getCapacity());
        }

        User gerard = User.register(
            new Email("gerard@trotti.ul"),
            new Password("G3R4rdIsTh3B3st!"),
            "Gerard Jugnot",
            "1968-05-03",
            "H",
            "gejug568",
            Role.TECHNICIAN
        );
        this.userRepository.save(gerard);
    }

    public static ApplicationContext getInstance() {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
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

    public PurchasePassUseCase getPurchasePassUseCase() {
        return purchasePassUseCase;
    }

    public GetUserPassesUseCase getGetUserPassesUseCase() {
        return getUserPassesUseCase;
    }

    public RequestRideCodeUseCase getRequestRideCodeUseCase() {
        return requestRideCodeUseCase;
    }

    public StartRideUseCase getStartRideUseCase() {
        return startRideUseCase;
    }

    public EndRideUseCase getEndRideUseCase() {
        return endRideUseCase;
    }

    public StartStationMaintenanceUseCase getStartStationMaintenanceUseCase() {
        return startStationMaintenanceUseCase;
    }

    public EndStationMaintenanceUseCase getEndStationMaintenanceUseCase() {
        return endStationMaintenanceUseCase;
    }

    public RequestStationMaintenanceUseCase getRequestStationMaintenanceUseCase() {
        return requestStationMaintenanceUseCase;
    }

    public StartTransferScooterUseCase getStartTransferScooterUseCase() {
        return startTransferScooterUseCase;
    }

    public EndTransferScooterUseCase getEndTransferScooterUseCase() {
        return endTransferScooterUseCase;
    }

    public GetRideHistoryUseCase getGetRideHistoryUseCase() {
        return getRideHistoryUseCase;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public TokenGenerator getTokenGenerator() {
        return tokenGenerator;
    }
}

package ca.ulaval.glo4003.trotti.interfaces.rest.config;

import ca.ulaval.glo4003.trotti.application.billing.AutomaticPaymentUseCase;
import ca.ulaval.glo4003.trotti.application.billing.PayUserBalanceUseCase;
import ca.ulaval.glo4003.trotti.application.billing.SendPaymentReminderUseCase;
import ca.ulaval.glo4003.trotti.application.billing.UpdateAutoPayPreferenceUseCase;
import ca.ulaval.glo4003.trotti.application.pass.GetUserPassesUseCase;
import ca.ulaval.glo4003.trotti.application.pass.PurchasePassUseCase;
import ca.ulaval.glo4003.trotti.application.ride.EndRideUseCase;
import ca.ulaval.glo4003.trotti.application.ride.GetRideHistoryUseCase;
import ca.ulaval.glo4003.trotti.application.ride.GetTripHistoryWithSummaryUseCase;
import ca.ulaval.glo4003.trotti.application.ride.RequestRideCodeUseCase;
import ca.ulaval.glo4003.trotti.application.ride.StartRideUseCase;
import ca.ulaval.glo4003.trotti.application.scooter.EndTransferScooterUseCase;
import ca.ulaval.glo4003.trotti.application.scooter.StartTransferScooterUseCase;
import ca.ulaval.glo4003.trotti.application.station.EndStationMaintenanceUseCase;
import ca.ulaval.glo4003.trotti.application.station.RequestStationMaintenanceUseCase;
import ca.ulaval.glo4003.trotti.application.station.StartStationMaintenanceUseCase;
import ca.ulaval.glo4003.trotti.application.user.ActivateAccountUseCase;
import ca.ulaval.glo4003.trotti.application.user.GetAllUsersUseCase;
import ca.ulaval.glo4003.trotti.application.user.GetUserBalanceUseCase;
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
import ca.ulaval.glo4003.trotti.infrastructure.email.EmailConfig;
import ca.ulaval.glo4003.trotti.infrastructure.email.MailgunEmailAdapter;
import ca.ulaval.glo4003.trotti.infrastructure.persistence.memory.*;
import ca.ulaval.glo4003.trotti.infrastructure.security.JwtTokenService;
import ca.ulaval.glo4003.trotti.infrastructure.time.SystemClock;
import ca.ulaval.glo4003.trotti.interfaces.rest.security.AuthenticationService;

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
    private final AuthenticationService authenticationService;

    private final PricingPolicy pricingPolicy;
    private final RideCodeGenerator rideCodeGenerator;
    private final RideFeeCalculator rideFeeCalculator;
    private final DailyUsageRepository dailyUsageRepository;

    private final ca.ulaval.glo4003.trotti.domain.user.UserFactory userFactory;
    private final ca.ulaval.glo4003.trotti.domain.pass.PassFactory passFactory;

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final ActivateAccountUseCase activateAccountUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUserBalanceUseCase getUserBalanceUseCase;
    private final PayUserBalanceUseCase payUserBalanceUseCase;

    private final PurchasePassUseCase purchasePassUseCase;
    private final GetUserPassesUseCase getUserPassesUseCase;

    private final RequestRideCodeUseCase requestRideCodeUseCase;
    private final StartRideUseCase startRideUseCase;
    private final EndRideUseCase endRideUseCase;
    private final GetRideHistoryUseCase getRideHistoryUseCase;
    private final GetTripHistoryWithSummaryUseCase getTripHistoryWithSummaryUseCase;

    private final StartStationMaintenanceUseCase startStationMaintenanceUseCase;
    private final EndStationMaintenanceUseCase endStationMaintenanceUseCase;
    private final RequestStationMaintenanceUseCase requestStationMaintenanceUseCase;

    private final StartTransferScooterUseCase startTransferScooterUseCase;
    private final EndTransferScooterUseCase endTransferScooterUseCase;

    private final SendPaymentReminderUseCase sendPaymentReminderUseCase;
    private final UpdateAutoPayPreferenceUseCase updateAutoPayPreferenceUseCase;
    private final AutomaticPaymentUseCase automaticPaymentUseCase;

    private ApplicationContext() {
        this(null, null, null);
    }

    public ApplicationContext(EmailService emailService, TokenGenerator tokenGenerator, Clock clock) {
        if (emailService == null) {
            EmailConfig emailConfig = EmailConfig.fromEnvironment();
            this.emailService = new MailgunEmailAdapter(emailConfig);
        } else {
            this.emailService = emailService;
        }

        this.tokenGenerator = tokenGenerator != null ? tokenGenerator : new JwtTokenService();
        this.clock = clock != null ? clock : new SystemClock();
        this.authenticationService = new AuthenticationService(this.tokenGenerator);

        this.passRepository = new InMemoryPassRepository();
        this.rideCodeRepository = new InMemoryRideCodeRepository();

        ScooterContext scooterContext = new ScooterContext();
        this.scooterRepository = scooterContext.getScooterRepository();

        UserContext userContext = new UserContext(this.emailService, this.tokenGenerator, this.clock, passRepository, rideCodeRepository);
        this.userRepository = userContext.getUserRepository();

        StationContext stationContext = new StationContext(userRepository, this.emailService, scooterRepository);
        this.stationRepository = stationContext.getStationRepository();

        ScooterContext scooterFullContext = new ScooterContext(stationRepository, scooterRepository);
        PassContext passContext = new PassContext(userRepository, this.clock);
        RideContext rideContext = new RideContext(userRepository, stationRepository, scooterRepository, this.emailService, this.clock);
        this.rideRepository = rideContext.getRideRepository();
        BillingContext billingContext = new BillingContext(userRepository, this.emailService, this.clock);

        this.pricingPolicy = new PricingPolicy();
        this.rideCodeGenerator = new RideCodeGenerator(this.clock);
        this.rideFeeCalculator = new RideFeeCalculator();
        this.dailyUsageRepository = new InMemoryDailyUsageRepository();
        this.userFactory = new ca.ulaval.glo4003.trotti.domain.user.UserFactory();
        this.passFactory = new ca.ulaval.glo4003.trotti.domain.pass.PassFactory();

        this.registerUserUseCase = userContext.getRegisterUserUseCase();
        this.loginUserUseCase = userContext.getLoginUserUseCase();
        this.activateAccountUseCase = userContext.getActivateAccountUseCase();
        this.getAllUsersUseCase = userContext.getGetAllUsersUseCase();
        this.getUserBalanceUseCase = userContext.getGetUserBalanceUseCase();
        this.payUserBalanceUseCase = billingContext.getPayUserBalanceUseCase();
        this.purchasePassUseCase = passContext.getPurchasePassUseCase();
        this.getUserPassesUseCase = passContext.getGetUserPassesUseCase();
        this.startStationMaintenanceUseCase = stationContext.getStartStationMaintenanceUseCase();
        this.endStationMaintenanceUseCase = stationContext.getEndStationMaintenanceUseCase();
        this.requestStationMaintenanceUseCase = stationContext.getRequestStationMaintenanceUseCase();
        this.startTransferScooterUseCase = scooterFullContext.getStartTransferScooterUseCase();
        this.endTransferScooterUseCase = scooterFullContext.getEndTransferScooterUseCase();
        this.requestRideCodeUseCase = rideContext.getRequestRideCodeUseCase();
        this.startRideUseCase = rideContext.getStartRideUseCase();
        this.endRideUseCase = rideContext.getEndRideUseCase();
        this.getRideHistoryUseCase = rideContext.getGetRideHistoryUseCase();
        this.sendPaymentReminderUseCase = billingContext.getSendPaymentReminderUseCase();
        this.updateAutoPayPreferenceUseCase = billingContext.getUpdateAutoPayPreferenceUseCase();
        this.automaticPaymentUseCase = billingContext.getAutomaticPaymentUseCase();
        this.getTripHistoryWithSummaryUseCase = rideContext.getGetTripHistoryWithSummaryUseCase();
        initializeData();
    }

    private void initializeData() {
        stationRepository.initialize();

        InMemoryScooterRepository scooterRepo = (InMemoryScooterRepository) scooterRepository;
        for (Station station : stationRepository.findAll()) {
            scooterRepo.initializeScootersForStation(station.getId(), station.getCapacity());
        }

        User gerard = userFactory.createForRegistration(
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

    public static void setInstance(ApplicationContext context) {
        instance = context;
    }

    public static void resetInstance() {
        instance = null;
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

    public PayUserBalanceUseCase getPayUserBalanceUseCase() {
        return payUserBalanceUseCase;
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

    public SendPaymentReminderUseCase getSendPaymentReminderUseCase() {return sendPaymentReminderUseCase;}

    public UpdateAutoPayPreferenceUseCase getUpdateAutoPayPreferenceUseCase() {return updateAutoPayPreferenceUseCase;}

    public AutomaticPaymentUseCase getAutomaticPaymentUseCase() {return automaticPaymentUseCase;}

    public GetTripHistoryWithSummaryUseCase getGetTripHistoryWithSummaryUseCase() {
        return getTripHistoryWithSummaryUseCase;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public PassRepository getPassRepository() {
        return passRepository;
    }

    public RideCodeRepository getRideCodeRepository() {
        return rideCodeRepository;
    }

    public TokenGenerator getTokenGenerator() {
        return tokenGenerator;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public Clock getClock() {
        return clock;
    }
}

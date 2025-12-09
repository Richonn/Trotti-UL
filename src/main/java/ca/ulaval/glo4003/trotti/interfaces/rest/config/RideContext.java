package ca.ulaval.glo4003.trotti.interfaces.rest.config;

import ca.ulaval.glo4003.trotti.application.ride.*;
import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.ride.*;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.infrastructure.persistence.memory.InMemoryDailyUsageRepository;
import ca.ulaval.glo4003.trotti.infrastructure.persistence.memory.InMemoryRideCodeRepository;
import ca.ulaval.glo4003.trotti.infrastructure.persistence.memory.InMemoryRideRepository;

public class RideContext {
    private final RideRepository rideRepository;
    private final RideCodeRepository rideCodeRepository;
    private final DailyUsageRepository dailyUsageRepository;
    private final RequestRideCodeUseCase requestRideCodeUseCase;
    private final StartRideUseCase startRideUseCase;
    private final EndRideUseCase endRideUseCase;
    private final GetRideHistoryUseCase getRideHistoryUseCase;
    private final GetTripHistoryWithSummaryUseCase getTripHistoryWithSummaryUseCase;

    public RideContext(UserRepository userRepository, StationRepository stationRepository,
                       ScooterRepository scooterRepository, PassRepository passRepository,
                       EmailService emailService, Clock clock) {
        RideCodeGenerator rideCodeGenerator = new RideCodeGenerator(clock);
        RideFeeCalculator rideFeeCalculator = new RideFeeCalculator();

        this.rideRepository = new InMemoryRideRepository();
        this.rideCodeRepository = new InMemoryRideCodeRepository();
        this.dailyUsageRepository = new InMemoryDailyUsageRepository();

        this.requestRideCodeUseCase = new RequestRideCodeUseCase(userRepository, rideCodeGenerator, emailService, clock);
        this.startRideUseCase = new StartRideUseCase(userRepository, stationRepository, scooterRepository, rideRepository, clock);
        this.endRideUseCase = new EndRideUseCase(userRepository, rideRepository, scooterRepository, stationRepository, passRepository, rideFeeCalculator, dailyUsageRepository, clock);
        this.getRideHistoryUseCase = new GetRideHistoryUseCase(userRepository, rideRepository);
        this.getTripHistoryWithSummaryUseCase = new GetTripHistoryWithSummaryUseCase(userRepository, rideRepository, clock);
    }

    public RideRepository getRideRepository() {
        return rideRepository;
    }

    public RideCodeRepository getRideCodeRepository() {
        return rideCodeRepository;
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

    public GetRideHistoryUseCase getGetRideHistoryUseCase() {
        return getRideHistoryUseCase;
    }

    public GetTripHistoryWithSummaryUseCase getGetTripHistoryWithSummaryUseCase() {
        return getTripHistoryWithSummaryUseCase;
    }
}

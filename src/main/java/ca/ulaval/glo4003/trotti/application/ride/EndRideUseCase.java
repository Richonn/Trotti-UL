package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.application.ride.dto.RideEndResult;
import ca.ulaval.glo4003.trotti.domain.ride.*;
import ca.ulaval.glo4003.trotti.domain.ride.exception.NoActiveRideException;
import ca.ulaval.glo4003.trotti.domain.scooter.Scooter;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.scooter.exception.ScooterNotFoundException;
import ca.ulaval.glo4003.trotti.domain.station.Station;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.station.StationState;
import ca.ulaval.glo4003.trotti.domain.station.exception.StationNotFoundException;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;

import java.time.Duration;

public class EndRideUseCase {
    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final ScooterRepository scooterRepository;
    private final StationRepository stationRepository;
    private final RideFeeCalculator feeCalculator;
    private final DailyUsageRepository dailyUsageRepository;
    private final Clock clock;

    public EndRideUseCase(UserRepository userRepository, RideRepository rideRepository,
                          ScooterRepository scooterRepository, StationRepository stationRepository,
                          RideFeeCalculator feeCalculator,
                          DailyUsageRepository dailyUsageRepository, Clock clock) {
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.scooterRepository = scooterRepository;
        this.stationRepository = stationRepository;
        this.feeCalculator = feeCalculator;
        this.dailyUsageRepository = dailyUsageRepository;
        this.clock = clock;
    }

    public RideEndResult execute(String userEmail, String stationName, int slotNumber) {
        Email email = new Email(userEmail);
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        Ride ride = rideRepository.findActiveRideByUserId(user.getId())
                .orElseThrow(NoActiveRideException::new);

        Station station = stationRepository.findById(stationName)
                .orElseThrow(StationNotFoundException::new);

        if (station.getState() == StationState.UNDER_MAINTENANCE) {
            throw new IllegalStateException("Cannot end ride at a station under maintenance");
        }

        Scooter scooter = scooterRepository.findById(ride.getScooterId())
                .orElseThrow(ScooterNotFoundException::new);
        scooter.returnToStation(stationName, slotNumber);
        scooterRepository.save(scooter);

        ride.end(clock.now(), stationName);
        rideRepository.save(ride);

        Duration rideDuration = ride.getDuration(clock);
        long rideMinutes = rideDuration.toMinutes();

        int dailyUsage = dailyUsageRepository.getUsageForDay(user.getId(), clock.today());
        ca.ulaval.glo4003.trotti.domain.user.RideBillingResult billingResult = user.calculateRideBilling(rideDuration, dailyUsage, clock, feeCalculator);

        if (rideMinutes <= 15) {
            dailyUsageRepository.recordUsage(user.getId(), clock.today(), (int) rideMinutes);
        }

        if (billingResult.getAdditionalFees().getAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            userRepository.save(user);
        }

        return new RideEndResult(
                ride.getId(),
                rideDuration,
                billingResult.getAdditionalFees(),
                billingResult.isChargedImmediately(),
                billingResult.getMonthlyBalanceAfter()
        );
    }
}

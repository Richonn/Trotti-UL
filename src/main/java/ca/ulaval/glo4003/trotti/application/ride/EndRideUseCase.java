package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.domain.pass.Money;
import ca.ulaval.glo4003.trotti.domain.pass.Pass;
import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.ride.*;
import ca.ulaval.glo4003.trotti.domain.ride.exception.NoActiveRideException;
import ca.ulaval.glo4003.trotti.domain.scooter.Scooter;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.scooter.exception.ScooterNotFoundException;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.station.exception.StationNotFoundException;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;

import java.time.Duration;
import java.util.List;

public class EndRideUseCase {
    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final ScooterRepository scooterRepository;
    private final StationRepository stationRepository;
    private final PassRepository passRepository;
    private final RideFeeCalculator feeCalculator;
    private final DailyUsageRepository dailyUsageRepository;
    private final Clock clock;

    public EndRideUseCase(UserRepository userRepository, RideRepository rideRepository,
                          ScooterRepository scooterRepository, StationRepository stationRepository,
                          PassRepository passRepository, RideFeeCalculator feeCalculator,
                          DailyUsageRepository dailyUsageRepository, Clock clock) {
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.scooterRepository = scooterRepository;
        this.stationRepository = stationRepository;
        this.passRepository = passRepository;
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

        stationRepository.findById(stationName)
                .orElseThrow(StationNotFoundException::new);

        Scooter scooter = scooterRepository.findById(ride.getScooterId())
                .orElseThrow(ScooterNotFoundException::new);
        scooter.returnToStation(stationName, slotNumber);
        scooterRepository.save(scooter);

        ride.end(clock.now(), stationName);
        rideRepository.save(ride);

        Duration rideDuration = ride.getDuration(clock);
        long rideMinutes = rideDuration.toMinutes();

        List<Pass> userPasses = passRepository.findByUserId(user.getId());
        Pass activePass = userPasses.stream()
                .filter(pass -> pass.isActiveOn(clock.today()))
                .findFirst()
                .orElse(null);

        Money additionalFees = Money.zero();
        boolean chargedImmediately = false;
        Money monthlyBalanceAfter = user.getMonthlyBalance().getAmount();

        int dailyQuota = user.getDailyQuota(activePass);

        if (rideMinutes <= 15) {
            int usedToday = dailyUsageRepository.getUsageForDay(user.getId(), clock.today());
            int newUsedToday = usedToday + (int) rideMinutes;

            if (newUsedToday > dailyQuota) {
                additionalFees = feeCalculator.getQuotaExceededFee();
            }

            dailyUsageRepository.recordUsage(user.getId(), clock.today(), (int) rideMinutes);
        } else {
            additionalFees = feeCalculator.calculateAdditionalFees(rideDuration);
        }

        if (additionalFees.getAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            if (activePass != null && activePass.isMonthlyBilling()) {
                user.addToMonthlyBalance(additionalFees);
                monthlyBalanceAfter = user.getMonthlyBalance().getAmount();
                chargedImmediately = false;
            } else {
                chargedImmediately = true;
            }
            userRepository.save(user);
        }

        return new RideEndResult(
                ride.getId(),
                rideDuration,
                additionalFees,
                chargedImmediately,
                monthlyBalanceAfter
        );
    }
}

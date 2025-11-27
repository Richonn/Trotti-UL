package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.domain.ride.Ride;
import ca.ulaval.glo4003.trotti.domain.ride.RideRepository;
import ca.ulaval.glo4003.trotti.domain.ride.TripSummary;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public class GetTripHistoryWithSummaryUseCase {
    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final Clock clock;

    public GetTripHistoryWithSummaryUseCase(UserRepository userRepository, RideRepository rideRepository, Clock clock) {
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.clock = clock;
    }

    public TripHistoryResult execute(String userEmail) {
        Email email = new Email(userEmail);
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        LocalDate today = clock.today();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Ride> rides = rideRepository.findCompletedRidesByUserIdInDateRange(
                user.getId(),
                startOfMonth,
                endOfMonth
        );

        TripSummary summary = TripSummary.from(rides);

        return new TripHistoryResult(rides, summary);
    }
}

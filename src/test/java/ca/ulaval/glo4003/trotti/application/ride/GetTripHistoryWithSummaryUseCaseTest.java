package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.application.ride.dto.TripHistoryResult;
import ca.ulaval.glo4003.trotti.domain.ride.Ride;
import ca.ulaval.glo4003.trotti.domain.ride.RideRepository;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.Password;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTripHistoryWithSummaryUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RideRepository rideRepository;

    @Mock
    private Clock clock;

    private GetTripHistoryWithSummaryUseCase useCase;
    private String userEmail;
    private User user;

    @BeforeEach
    void setUp() {
        useCase = new GetTripHistoryWithSummaryUseCase(userRepository, rideRepository, clock);
        userEmail = "test@example.com";
        user = User.register(
                new Email("test@example.com"),
                new Password("ValidPass123!"),
                "Test User",
                "2000-01-01",
                "H",
                "AAAA00000001",
                Role.USER
        );
    }

    @Test
    void givenValidUser_whenExecute_thenReturnsTripsForCurrentMonth() {
        LocalDate today = LocalDate.of(2025, 1, 15);
        when(clock.today()).thenReturn(today);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        Ride ride1 = Ride.start(user.getId(), "scooter-1", "VACHON", LocalDateTime.of(2025, 1, 10, 10, 0));
        ride1.end(LocalDateTime.of(2025, 1, 10, 10, 30), "PEPS");

        Ride ride2 = Ride.start(user.getId(), "scooter-2", "PEPS", LocalDateTime.of(2025, 1, 12, 14, 0));
        ride2.end(LocalDateTime.of(2025, 1, 12, 14, 45), "VACHON");

        List<Ride> expectedRides = Arrays.asList(ride1, ride2);
        when(rideRepository.findCompletedRidesByUserIdInDateRange(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedRides);

        TripHistoryResult result = useCase.execute(userEmail);

        assertThat(result.getRides()).hasSize(2);
        assertThat(result.getRides()).containsExactlyElementsOf(expectedRides);
        verify(rideRepository).findCompletedRidesByUserIdInDateRange(
                user.getId(),
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 1, 31, 23, 59, 59)
        );
    }

    @Test
    void givenValidUser_whenExecute_thenReturnsSummaryWithCorrectStatistics() {
        LocalDate today = LocalDate.of(2025, 1, 15);
        when(clock.today()).thenReturn(today);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        Ride ride1 = Ride.start(user.getId(), "scooter-1", "VACHON", LocalDateTime.of(2025, 1, 10, 10, 0));
        ride1.end(LocalDateTime.of(2025, 1, 10, 10, 30), "PEPS");

        Ride ride2 = Ride.start(user.getId(), "scooter-2", "VACHON", LocalDateTime.of(2025, 1, 12, 14, 0));
        ride2.end(LocalDateTime.of(2025, 1, 12, 14, 45), "PEPS");

        Ride ride3 = Ride.start(user.getId(), "scooter-3", "PEPS", LocalDateTime.of(2025, 1, 14, 9, 0));
        ride3.end(LocalDateTime.of(2025, 1, 14, 9, 15), "VACHON");

        List<Ride> expectedRides = Arrays.asList(ride1, ride2, ride3);
        when(rideRepository.findCompletedRidesByUserIdInDateRange(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedRides);

        TripHistoryResult result = useCase.execute(userEmail);

        assertThat(result.getSummary().getTotalTrips()).isEqualTo(3);
        assertThat(result.getSummary().getTotalDurationMinutes()).isEqualTo(90);
        assertThat(result.getSummary().getAverageDurationMinutes()).isEqualTo(30);
        assertThat(result.getSummary().getFavoriteStationId()).isIn("VACHON", "PEPS");
    }

    @Test
    void givenUserNotFound_whenExecute_thenThrowsUserNotFoundException() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userEmail))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void givenUserWithNoTripsInMonth_whenExecute_thenReturnsEmptyListAndZeroSummary() {
        LocalDate today = LocalDate.of(2025, 1, 15);
        when(clock.today()).thenReturn(today);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findCompletedRidesByUserIdInDateRange(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList());

        TripHistoryResult result = useCase.execute(userEmail);

        assertThat(result.getRides()).isEmpty();
        assertThat(result.getSummary().getTotalTrips()).isZero();
        assertThat(result.getSummary().getTotalDurationMinutes()).isZero();
        assertThat(result.getSummary().getAverageDurationMinutes()).isZero();
        assertThat(result.getSummary().getFavoriteStationId()).isNull();
    }

    @Test
    void givenEndOfMonth_whenExecute_thenCalculatesCorrectDateRange() {
        LocalDate endOfJanuary = LocalDate.of(2025, 1, 31);
        when(clock.today()).thenReturn(endOfJanuary);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findCompletedRidesByUserIdInDateRange(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList());

        useCase.execute(userEmail);

        verify(rideRepository).findCompletedRidesByUserIdInDateRange(
                user.getId(),
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 1, 31, 23, 59, 59)
        );
    }

    @Test
    void givenStartOfMonth_whenExecute_thenCalculatesCorrectDateRange() {
        LocalDate startOfFebruary = LocalDate.of(2025, 2, 1);
        when(clock.today()).thenReturn(startOfFebruary);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findCompletedRidesByUserIdInDateRange(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList());

        useCase.execute(userEmail);

        verify(rideRepository).findCompletedRidesByUserIdInDateRange(
                user.getId(),
                LocalDateTime.of(2025, 2, 1, 0, 0),
                LocalDateTime.of(2025, 2, 28, 23, 59, 59)
        );
    }

    @Test
    void givenLeapYearFebruary_whenExecute_thenHandles29Days() {
        LocalDate leapYearFebruary = LocalDate.of(2024, 2, 15);
        when(clock.today()).thenReturn(leapYearFebruary);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findCompletedRidesByUserIdInDateRange(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList());

        useCase.execute(userEmail);

        verify(rideRepository).findCompletedRidesByUserIdInDateRange(
                user.getId(),
                LocalDateTime.of(2024, 2, 1, 0, 0),
                LocalDateTime.of(2024, 2, 29, 23, 59, 59)
        );
    }

    @Test
    void givenNonLeapYearFebruary_whenExecute_thenHandles28Days() {
        LocalDate nonLeapYearFebruary = LocalDate.of(2025, 2, 15);
        when(clock.today()).thenReturn(nonLeapYearFebruary);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findCompletedRidesByUserIdInDateRange(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList());

        useCase.execute(userEmail);

        verify(rideRepository).findCompletedRidesByUserIdInDateRange(
                user.getId(),
                LocalDateTime.of(2025, 2, 1, 0, 0),
                LocalDateTime.of(2025, 2, 28, 23, 59, 59)
        );
    }

    @Test
    void givenUserRequestingHistory_whenExecute_thenReturnsOnlyTheirOwnTrips() {
        LocalDate today = LocalDate.of(2025, 1, 15);
        when(clock.today()).thenReturn(today);

        User userA = User.register(
                new Email("userA@test.com"),
                new Password("ValidPass123!"),
                "User A",
                "2000-01-01",
                "H",
                "AAAA00000001",
                Role.USER
        );

        User userB = User.register(
                new Email("userB@test.com"),
                new Password("ValidPass123!"),
                "User B",
                "2000-01-01",
                "F",
                "BBBB00000002",
                Role.USER
        );

        Ride userARide = Ride.start(userA.getId(), "scooter-1", "VACHON", LocalDateTime.of(2025, 1, 10, 10, 0));
        userARide.end(LocalDateTime.of(2025, 1, 10, 10, 30), "PEPS");

        Ride userBRide = Ride.start(userB.getId(), "scooter-2", "PEPS", LocalDateTime.of(2025, 1, 10, 11, 0));
        userBRide.end(LocalDateTime.of(2025, 1, 10, 11, 30), "VACHON");

        when(userRepository.findByEmail(new Email("userA@test.com"))).thenReturn(Optional.of(userA));
        when(rideRepository.findCompletedRidesByUserIdInDateRange(
                userA.getId(),
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 1, 31, 23, 59, 59)
        )).thenReturn(Arrays.asList(userARide));

        TripHistoryResult result = useCase.execute("userA@test.com");

        assertThat(result.getRides()).hasSize(1);
        assertThat(result.getRides().get(0).getUserId()).isEqualTo(userA.getId());
        assertThat(result.getRides().get(0).getUserId()).isNotEqualTo(userB.getId());
        verify(rideRepository).findCompletedRidesByUserIdInDateRange(
                userA.getId(),
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 1, 31, 23, 59, 59)
        );
    }
}

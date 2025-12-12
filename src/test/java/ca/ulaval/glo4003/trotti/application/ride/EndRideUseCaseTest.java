package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.application.ride.dto.RideEndResult;
import ca.ulaval.glo4003.trotti.domain.pass.*;
import ca.ulaval.glo4003.trotti.domain.ride.*;
import ca.ulaval.glo4003.trotti.domain.ride.exception.NoActiveRideException;
import ca.ulaval.glo4003.trotti.domain.scooter.Scooter;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.scooter.exception.ScooterNotFoundException;
import ca.ulaval.glo4003.trotti.domain.station.Station;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.station.exception.StationNotFoundException;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.Password;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.infrastructure.persistence.memory.InMemoryDailyUsageRepository;
import ca.ulaval.glo4003.trotti.infrastructure.time.FixedClock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EndRideUseCaseTest {

    private EndRideUseCase endRideUseCase;
    private UserRepository userRepository;
    private RideRepository rideRepository;
    private ScooterRepository scooterRepository;
    private StationRepository stationRepository;
    private RideFeeCalculator feeCalculator;
    private Clock clock;
    private ca.ulaval.glo4003.trotti.domain.shared.EmailService emailService;

    private User user;
    private Ride ride;
    private Scooter scooter;
    private Station station;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        rideRepository = mock(RideRepository.class);
        scooterRepository = mock(ScooterRepository.class);
        stationRepository = mock(StationRepository.class);
        feeCalculator = mock(RideFeeCalculator.class);
        emailService = mock(ca.ulaval.glo4003.trotti.domain.shared.EmailService.class);
        clock = new FixedClock(LocalDateTime.of(2025, 11, 16, 14, 0));
        DailyUsageRepository dailyUsageRepository = new InMemoryDailyUsageRepository();

        endRideUseCase = new EndRideUseCase(
                userRepository,
                rideRepository,
                scooterRepository,
                stationRepository,
                feeCalculator,
                dailyUsageRepository,
                clock
        );

        user = User.register(
                new Email("test@example.com"),
                new Password("ValidPass123!"),
                "Test User",
                "2000-01-01",
                "H",
                "TEUS12345678",
                Role.USER

        );
        user.activate(emailService);

        scooter = new Scooter("scooter-1", 85.0, "Station A", 1);
        station = new Station("Station B", "Campus Location", 10);
        ride = Ride.start(user.getId(), scooter.getId(), "Station A", clock.now().minusMinutes(20));
    }

    @Test
    void givenValidRequest_whenEndingRide_thenRideIsEnded() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.of(ride));
        when(stationRepository.findById("Station B")).thenReturn(Optional.of(station));
        when(scooterRepository.findById(scooter.getId())).thenReturn(Optional.of(scooter));
        when(feeCalculator.calculateAdditionalFees(any(Duration.class))).thenReturn(Money.zero());

        RideEndResult result = endRideUseCase.execute("test@example.com", "Station B", 2);

        verify(rideRepository).save(any(Ride.class));
        verify(scooterRepository).save(any(Scooter.class));
        assertThat(result).isNotNull();
        assertThat(result.getDurationInMinutes()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void givenUserNotFound_whenEndingRide_thenThrowsUserNotFoundException() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> endRideUseCase.execute("test@example.com", "Station B", 2));
    }

    @Test
    void givenNoActiveRide_whenEndingRide_thenThrowsNoActiveRideException() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(NoActiveRideException.class,
                () -> endRideUseCase.execute("test@example.com", "Station B", 2));
    }

    @Test
    void givenStationNotFound_whenEndingRide_thenThrowsStationNotFoundException() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.of(ride));
        when(stationRepository.findById("Station B")).thenReturn(Optional.empty());

        assertThrows(StationNotFoundException.class,
                () -> endRideUseCase.execute("test@example.com", "Station B", 2));
    }

    @Test
    void givenScooterNotFound_whenEndingRide_thenThrowsScooterNotFoundException() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.of(ride));
        when(stationRepository.findById("Station B")).thenReturn(Optional.of(station));
        when(scooterRepository.findById(scooter.getId())).thenReturn(Optional.empty());

        assertThrows(ScooterNotFoundException.class,
                () -> endRideUseCase.execute("test@example.com", "Station B", 2));
    }

    @Test
    void givenRideWithAdditionalFeesAndMonthlyBilling_whenEndingRide_thenFeesAddedToMonthlyBalance() {
        Money additionalFees = new Money(new BigDecimal("1.50"));
        Pass monthlyPass = Pass.purchase(
                user.getId(),
                Session.getInstance("A2025"),
                0,
                true,
                new PricingPolicy());
        user.addPass(monthlyPass);

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.of(ride));
        when(stationRepository.findById("Station B")).thenReturn(Optional.of(station));
        when(scooterRepository.findById(scooter.getId())).thenReturn(Optional.of(scooter));
        when(feeCalculator.calculateAdditionalFees(any(Duration.class))).thenReturn(additionalFees);

        RideEndResult result = endRideUseCase.execute("test@example.com", "Station B", 2);

        verify(userRepository).save(user);
        assertThat(result.isChargedImmediately()).isFalse();
        assertThat(result.getAdditionalFees()).isEqualTo(additionalFees);
        assertThat(result.getMonthlyBalanceAfter().getAmount()).isEqualByComparingTo(additionalFees.getAmount());
    }

    @Test
    void givenRideWithAdditionalFeesAndImmediateBilling_whenEndingRide_thenFeesChargedImmediately() {
        Money additionalFees = new Money(new BigDecimal("2.25"));
        Pass immediatePass = Pass.purchase(
                user.getId(),
                Session.getInstance("A2025"),
                0,
                false,
                new PricingPolicy());
        user.addPass(immediatePass);

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.of(ride));
        when(stationRepository.findById("Station B")).thenReturn(Optional.of(station));
        when(scooterRepository.findById(scooter.getId())).thenReturn(Optional.of(scooter));
        when(feeCalculator.calculateAdditionalFees(any(Duration.class))).thenReturn(additionalFees);

        RideEndResult result = endRideUseCase.execute("test@example.com", "Station B", 2);

        assertThat(result.isChargedImmediately()).isTrue();
        assertThat(result.getAdditionalFees()).isEqualTo(additionalFees);
    }

    @Test
    void givenRideWithNoAdditionalFees_whenEndingRide_thenNoFeesCharged() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.of(ride));
        when(stationRepository.findById("Station B")).thenReturn(Optional.of(station));
        when(scooterRepository.findById(scooter.getId())).thenReturn(Optional.of(scooter));
        when(feeCalculator.calculateAdditionalFees(any(Duration.class))).thenReturn(Money.zero());

        RideEndResult result = endRideUseCase.execute("test@example.com", "Station B", 2);

        verify(userRepository, never()).save(user);
        assertThat(result.getAdditionalFees().getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}

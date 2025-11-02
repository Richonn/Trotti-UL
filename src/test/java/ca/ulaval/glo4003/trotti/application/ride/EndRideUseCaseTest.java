package ca.ulaval.glo4003.trotti.application.ride;

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
    private PassRepository passRepository;
    private RideFeeCalculator feeCalculator;

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
        passRepository = mock(PassRepository.class);
        feeCalculator = mock(RideFeeCalculator.class);

        endRideUseCase = new EndRideUseCase(
                userRepository,
                rideRepository,
                scooterRepository,
                stationRepository,
                passRepository,
                feeCalculator
        );

        user = User.register(
                new Email("test@example.com"),
                new Password("ValidPass123!"),
                "Test User",
                "2000-01-01",
                "H",
                "TEUS12345678"
        );
        user.activate();

        scooter = new Scooter("scooter-1", 85.0, "Station A", 1);
        station = new Station("Station B", "Campus Location", 10);
        ride = Ride.start(user.getId(), scooter.getId(), LocalDateTime.now().minusMinutes(20));
    }

    @Test
    void givenValidRequest_whenEndingRide_thenRideIsEnded() {
        // Given
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.of(ride));
        when(stationRepository.findById("Station B")).thenReturn(Optional.of(station));
        when(scooterRepository.findById(scooter.getId())).thenReturn(Optional.of(scooter));
        when(feeCalculator.calculateAdditionalFees(any(Duration.class))).thenReturn(Money.zero());
        when(passRepository.findByUserId(user.getId())).thenReturn(List.of());

        // When
        RideEndResult result = endRideUseCase.execute("test@example.com", "Station B", 2);

        // Then
        verify(rideRepository).save(any(Ride.class));
        verify(scooterRepository).save(any(Scooter.class));
        assertThat(result).isNotNull();
        assertThat(result.getDurationInMinutes()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void givenUserNotFound_whenEndingRide_thenThrowsUserNotFoundException() {
        // Given
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        // When / Then
        assertThrows(UserNotFoundException.class,
                () -> endRideUseCase.execute("test@example.com", "Station B", 2));
    }

    @Test
    void givenNoActiveRide_whenEndingRide_thenThrowsNoActiveRideException() {
        // Given
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(NoActiveRideException.class,
                () -> endRideUseCase.execute("test@example.com", "Station B", 2));
    }

    @Test
    void givenStationNotFound_whenEndingRide_thenThrowsStationNotFoundException() {
        // Given
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.of(ride));
        when(stationRepository.findById("Station B")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(StationNotFoundException.class,
                () -> endRideUseCase.execute("test@example.com", "Station B", 2));
    }

    @Test
    void givenScooterNotFound_whenEndingRide_thenThrowsScooterNotFoundException() {
        // Given
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.of(ride));
        when(stationRepository.findById("Station B")).thenReturn(Optional.of(station));
        when(scooterRepository.findById(scooter.getId())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ScooterNotFoundException.class,
                () -> endRideUseCase.execute("test@example.com", "Station B", 2));
    }

    @Test
    void givenRideWithAdditionalFeesAndMonthlyBilling_whenEndingRide_thenFeesAddedToMonthlyBalance() {
        // Given
        Money additionalFees = new Money(new BigDecimal("1.50"));
        Pass monthlyPass = Pass.purchase(
                user.getId(),
                Session.getInstance("A2025"),
                0,
                true,  // monthly billing
                new PricingPolicy()
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.of(ride));
        when(stationRepository.findById("Station B")).thenReturn(Optional.of(station));
        when(scooterRepository.findById(scooter.getId())).thenReturn(Optional.of(scooter));
        when(feeCalculator.calculateAdditionalFees(any(Duration.class))).thenReturn(additionalFees);
        when(passRepository.findByUserId(user.getId())).thenReturn(List.of(monthlyPass));

        // When
        RideEndResult result = endRideUseCase.execute("test@example.com", "Station B", 2);

        // Then
        verify(userRepository).save(user);
        assertThat(result.isChargedImmediately()).isFalse();
        assertThat(result.getAdditionalFees()).isEqualTo(additionalFees);
        assertThat(result.getMonthlyBalanceAfter().getAmount()).isEqualByComparingTo(additionalFees.getAmount());
    }

    @Test
    void givenRideWithAdditionalFeesAndImmediateBilling_whenEndingRide_thenFeesChargedImmediately() {
        // Given
        Money additionalFees = new Money(new BigDecimal("2.25"));
        Pass immediatePass = Pass.purchase(
                user.getId(),
                Session.getInstance("A2025"),
                0,
                false,  // immediate billing
                new PricingPolicy()
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.of(ride));
        when(stationRepository.findById("Station B")).thenReturn(Optional.of(station));
        when(scooterRepository.findById(scooter.getId())).thenReturn(Optional.of(scooter));
        when(feeCalculator.calculateAdditionalFees(any(Duration.class))).thenReturn(additionalFees);
        when(passRepository.findByUserId(user.getId())).thenReturn(List.of(immediatePass));

        // When
        RideEndResult result = endRideUseCase.execute("test@example.com", "Station B", 2);

        // Then
        assertThat(result.isChargedImmediately()).isTrue();
        assertThat(result.getAdditionalFees()).isEqualTo(additionalFees);
    }

    @Test
    void givenRideWithNoAdditionalFees_whenEndingRide_thenNoFeesCharged() {
        // Given
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.of(ride));
        when(stationRepository.findById("Station B")).thenReturn(Optional.of(station));
        when(scooterRepository.findById(scooter.getId())).thenReturn(Optional.of(scooter));
        when(feeCalculator.calculateAdditionalFees(any(Duration.class))).thenReturn(Money.zero());
        when(passRepository.findByUserId(user.getId())).thenReturn(List.of());

        // When
        RideEndResult result = endRideUseCase.execute("test@example.com", "Station B", 2);

        // Then
        verify(userRepository, never()).save(user);
        assertThat(result.getAdditionalFees().getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}

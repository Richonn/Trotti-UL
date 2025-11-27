package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.domain.pass.*;
import ca.ulaval.glo4003.trotti.domain.ride.*;
import ca.ulaval.glo4003.trotti.domain.scooter.Scooter;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.station.Station;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.Password;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.infrastructure.persistence.memory.InMemoryDailyUsageRepository;
import ca.ulaval.glo4003.trotti.infrastructure.time.FixedClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EndRideWithQuotaUseCaseTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RideRepository rideRepository;
    @Mock
    private ScooterRepository scooterRepository;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private PassRepository passRepository;

    private RideFeeCalculator feeCalculator;
    private DailyUsageRepository dailyUsageRepository;
    private FixedClock clock;
    private EndRideUseCase endRideUseCase;
    private ca.ulaval.glo4003.trotti.domain.shared.EmailService emailService;

    private User user;
    private Scooter scooter;
    private Station startStation;
    private Station endStation;
    private Pass activePass;

    @BeforeEach
    void setUp() {
        feeCalculator = new RideFeeCalculator();
        dailyUsageRepository = new InMemoryDailyUsageRepository();
        clock = new FixedClock(LocalDateTime.of(2026, 2, 16, 14, 0));
        emailService = mock(ca.ulaval.glo4003.trotti.domain.shared.EmailService.class);

        endRideUseCase = new EndRideUseCase(
                userRepository,
                rideRepository,
                scooterRepository,
                stationRepository,
                passRepository,
                feeCalculator,
                dailyUsageRepository,
                clock
        );

        user = User.register(
                new Email("alexandra@example.com"),
                new Password("ValidPass123!"),
                "Alexandra",
                "2000-01-01",
                "F",
                "ALEX12345678",
                Role.USER
        );
        user.activate(emailService);

        scooter = new Scooter("scooter-1", 85.0, "VACHON", 1);
        startStation = new Station("VACHON", "Campus Location", 10);
        endStation = new Station("PEPS", "Campus Location", 10);

        activePass = Pass.purchase(user.getId(), Session.H2026, 20, true, new PricingPolicy());
    }

    @Test
    void givenRideUnder15Minutes_whenEndingRide_thenNoAdditionalFees() {
        Ride ride = Ride.start(user.getId(), scooter.getId(), "VACHON", clock.now().minusMinutes(10));

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.of(ride));
        when(stationRepository.findById("PEPS")).thenReturn(Optional.of(endStation));
        when(scooterRepository.findById(scooter.getId())).thenReturn(Optional.of(scooter));
        when(passRepository.findByUserId(user.getId())).thenReturn(List.of(activePass));

        RideEndResult result = endRideUseCase.execute("alexandra@example.com", "PEPS", 2);

        assertThat(result.getAdditionalFeesAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getDurationInMinutes()).isEqualTo(10);
    }

    @Test
    void givenRideExactly15Minutes_whenEndingRide_thenNoAdditionalFees() {
        Ride ride = Ride.start(user.getId(), scooter.getId(), "VACHON", clock.now().minusMinutes(15));

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.of(ride));
        when(stationRepository.findById("PEPS")).thenReturn(Optional.of(endStation));
        when(scooterRepository.findById(scooter.getId())).thenReturn(Optional.of(scooter));
        when(passRepository.findByUserId(user.getId())).thenReturn(List.of(activePass));

        RideEndResult result = endRideUseCase.execute("alexandra@example.com", "PEPS", 2);

        assertThat(result.getAdditionalFeesAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getDurationInMinutes()).isEqualTo(15);
    }

    @Test
    void givenRideUnder15Minutes_whenQuotaExceeded_thenCharge5Dollars() {
        dailyUsageRepository.recordUsage(user.getId(), clock.today(), 45);

        Ride ride = Ride.start(user.getId(), scooter.getId(), "VACHON", clock.now().minusMinutes(10));

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.of(ride));
        when(stationRepository.findById("PEPS")).thenReturn(Optional.of(endStation));
        when(scooterRepository.findById(scooter.getId())).thenReturn(Optional.of(scooter));
        when(passRepository.findByUserId(user.getId())).thenReturn(List.of(activePass));

        RideEndResult result = endRideUseCase.execute("alexandra@example.com", "PEPS", 2);

        assertThat(result.getAdditionalFeesAmount()).isEqualByComparingTo(new BigDecimal("5.00"));
        assertThat(result.getDurationInMinutes()).isEqualTo(10);
    }

    @Test
    void givenRideUnder15Minutes_whenWithinQuota_thenNoFees() {
        dailyUsageRepository.recordUsage(user.getId(), clock.today(), 30);

        Ride ride = Ride.start(user.getId(), scooter.getId(), "VACHON", clock.now().minusMinutes(14));

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.of(ride));
        when(stationRepository.findById("PEPS")).thenReturn(Optional.of(endStation));
        when(scooterRepository.findById(scooter.getId())).thenReturn(Optional.of(scooter));
        when(passRepository.findByUserId(user.getId())).thenReturn(List.of(activePass));

        RideEndResult result = endRideUseCase.execute("alexandra@example.com", "PEPS", 2);

        assertThat(result.getAdditionalFeesAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getDurationInMinutes()).isEqualTo(14);
    }

    @Test
    void givenRideOver15Minutes_whenEndingRide_thenChargeTimeBasedFees() {
        Ride ride = Ride.start(user.getId(), scooter.getId(), "VACHON", clock.now().minusMinutes(20));

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findActiveRideByUserId(user.getId())).thenReturn(Optional.of(ride));
        when(stationRepository.findById("PEPS")).thenReturn(Optional.of(endStation));
        when(scooterRepository.findById(scooter.getId())).thenReturn(Optional.of(scooter));
        when(passRepository.findByUserId(user.getId())).thenReturn(List.of(activePass));

        RideEndResult result = endRideUseCase.execute("alexandra@example.com", "PEPS", 2);

        assertThat(result.getAdditionalFeesAmount()).isEqualByComparingTo(new BigDecimal("0.38"));
        assertThat(result.getDurationInMinutes()).isEqualTo(20);
    }
}

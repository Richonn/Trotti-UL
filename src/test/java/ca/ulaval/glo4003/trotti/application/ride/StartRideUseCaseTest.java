package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.domain.pass.Pass;
import ca.ulaval.glo4003.trotti.domain.pass.PricingPolicy;
import ca.ulaval.glo4003.trotti.domain.pass.Session;
import ca.ulaval.glo4003.trotti.domain.pass.exception.NoActivePassException;
import ca.ulaval.glo4003.trotti.domain.ride.Ride;
import ca.ulaval.glo4003.trotti.domain.ride.RideCode;
import ca.ulaval.glo4003.trotti.domain.ride.RideCodeRepository;
import ca.ulaval.glo4003.trotti.domain.ride.RideRepository;
import ca.ulaval.glo4003.trotti.domain.ride.exception.ExpiredRideCodeException;
import ca.ulaval.glo4003.trotti.domain.ride.exception.InvalidRideCodeException;
import ca.ulaval.glo4003.trotti.domain.scooter.Scooter;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.scooter.exception.LowBatteryException;
import ca.ulaval.glo4003.trotti.domain.station.Station;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.station.exception.SlotNotAvailableException;
import ca.ulaval.glo4003.trotti.domain.station.exception.StationNotFoundException;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.Password;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.AccountNotActivatedException;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.infrastructure.time.FixedClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StartRideUseCaseTest {

    private StartRideUseCase startRideUseCase;
    private UserRepository userRepository;
    private StationRepository stationRepository;
    private ScooterRepository scooterRepository;
    private RideRepository rideRepository;
    private RideCodeRepository rideCodeRepository;
    private Clock clock;
    private ca.ulaval.glo4003.trotti.domain.shared.EmailService emailService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        stationRepository = mock(StationRepository.class);
        scooterRepository = mock(ScooterRepository.class);
        rideRepository = mock(RideRepository.class);
        rideCodeRepository = mock(RideCodeRepository.class);
        emailService = mock(ca.ulaval.glo4003.trotti.domain.shared.EmailService.class);
        clock = new FixedClock(LocalDate.of(2025, 11, 16));

        startRideUseCase = new StartRideUseCase(
            userRepository, stationRepository,
            scooterRepository, rideRepository, rideCodeRepository, clock
        );
    }

    @Test
    void givenValidRequest_whenStartingRide_thenSucceeds() {
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123",
            Role.USER
        );
        user.activate(emailService);

        Station station = new Station("station-1", "Campus", 20);
        Scooter scooter = new Scooter("scooter-1", 80.0, "station-1", 5);
        station.addScooter(scooter);
        RideCode rideCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));

        Pass activePass = Pass.purchase(user.getId(), Session.A2025, 0, false, new PricingPolicy());
        user.addPass(activePass);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideCodeRepository.findByUserEmail(email)).thenReturn(Optional.of(rideCode));
        when(stationRepository.findById("station-1")).thenReturn(Optional.of(station));
        when(scooterRepository.findById("scooter-1")).thenReturn(Optional.of(scooter));

        RideStartResult result = startRideUseCase.execute(email, "station-1", 5, "ABC123");

        assertNotNull(result);
        assertEquals(80.0, result.getBatteryLevel());
        verify(stationRepository).save(station);
        verify(rideRepository).save(any(Ride.class));
    }

    @Test
    void givenNonExistentUser_whenStartingRide_thenThrowsUserNotFoundException() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
            startRideUseCase.execute("test@example.com", "station-1", 5, "ABC123")
        );
    }

    @Test
    void givenNonActivatedUser_whenStartingRide_thenThrowsAccountNotActivatedException() {
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123",
            Role.USER
        );

        Station station = new Station("station-1", "Campus", 20);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(stationRepository.findById("station-1")).thenReturn(Optional.of(station));

        assertThrows(AccountNotActivatedException.class, () ->
            startRideUseCase.execute(email, "station-1", 5, "ABC123")
        );
    }

    @Test
    void givenUserWithoutActivePass_whenStartingRide_thenThrowsNoActivePassException() {
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123",
            Role.USER
        );
        user.activate(emailService);

        Station station = new Station("station-1", "Campus", 20);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(stationRepository.findById("station-1")).thenReturn(Optional.of(station));

        assertThrows(NoActivePassException.class, () ->
            startRideUseCase.execute(email, "station-1", 5, "ABC123")
        );
    }

    @Test
    void givenInvalidRideCode_whenStartingRide_thenThrowsInvalidRideCodeException() {
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123",
            Role.USER
        );
        user.activate(emailService);

        RideCode rideCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));
        Station station = new Station("station-1", "Campus", 20);

        Pass activePass = Pass.purchase(user.getId(), Session.A2025, 0, false, new PricingPolicy());
        user.addPass(activePass);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideCodeRepository.findByUserEmail(email)).thenReturn(Optional.of(rideCode));
        when(stationRepository.findById("station-1")).thenReturn(Optional.of(station));

        assertThrows(InvalidRideCodeException.class, () ->
            startRideUseCase.execute(email, "station-1", 5, "WRONG123")
        );
    }

    @Test
    void givenExpiredRideCode_whenStartingRide_thenThrowsExpiredRideCodeException() {
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123",
            Role.USER
        );
        user.activate(emailService);

        RideCode expiredCode = new RideCode("ABC123", clock.now().minusMinutes(1));
        Pass activePass = Pass.purchase(user.getId(), Session.A2025, 0, false, new PricingPolicy());
        user.addPass(activePass);
        Station station = new Station("station-1", "Campus", 20);

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideCodeRepository.findByUserEmail(email)).thenReturn(Optional.of(expiredCode));
        when(stationRepository.findById("station-1")).thenReturn(Optional.of(station));

        assertThrows(ExpiredRideCodeException.class, () ->
            startRideUseCase.execute(email, "station-1", 5, "ABC123")
        );
    }

    @Test
    void givenNonExistentStation_whenStartingRide_thenThrowsStationNotFoundException() {
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123",
            Role.USER
        );
        user.activate(emailService);

        RideCode rideCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));

        Pass activePass = Pass.purchase(user.getId(), Session.A2025, 0, false, new PricingPolicy());
        user.addPass(activePass);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideCodeRepository.findByUserEmail(email)).thenReturn(Optional.of(rideCode));
        when(stationRepository.findById("station-1")).thenReturn(Optional.empty());

        assertThrows(StationNotFoundException.class, () ->
            startRideUseCase.execute(email, "station-1", 5, "ABC123")
        );
    }

    @Test
    void givenEmptySlot_whenStartingRide_thenThrowsSlotNotAvailableException() {
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123",
            Role.USER
        );
        user.activate(emailService);

        Station station = new Station("station-1", "Campus", 20);
        RideCode rideCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));

        Pass activePass = Pass.purchase(user.getId(), Session.A2025, 0, false, new PricingPolicy());
        user.addPass(activePass);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideCodeRepository.findByUserEmail(email)).thenReturn(Optional.of(rideCode));
        when(stationRepository.findById("station-1")).thenReturn(Optional.of(station));

        assertThrows(SlotNotAvailableException.class, () ->
            startRideUseCase.execute(email, "station-1", 5, "ABC123")
        );
    }

    @Test
    void givenScooterWithLowBattery_whenStartingRide_thenThrowsLowBatteryException() {
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123",
            Role.USER
        );
        user.activate(emailService);

        Station station = new Station("station-1", "Campus", 20);
        Scooter scooter = new Scooter("scooter-1", 10.0, "station-1", 5);
        station.addScooter(scooter);
        RideCode rideCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));

        Pass activePass = Pass.purchase(user.getId(), Session.A2025, 0, false, new PricingPolicy());
        user.addPass(activePass);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideCodeRepository.findByUserEmail(email)).thenReturn(Optional.of(rideCode));
        when(stationRepository.findById("station-1")).thenReturn(Optional.of(station));

        assertThrows(LowBatteryException.class, () ->
            startRideUseCase.execute(email, "station-1", 5, "ABC123")
        );
    }
}

package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.pass.exception.NoActivePassException;
import ca.ulaval.glo4003.trotti.domain.ride.*;
import ca.ulaval.glo4003.trotti.domain.ride.exception.ExpiredRideCodeException;
import ca.ulaval.glo4003.trotti.domain.ride.exception.InvalidRideCodeException;
import ca.ulaval.glo4003.trotti.domain.scooter.Scooter;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.station.Station;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.station.exception.SlotNotAvailableException;
import ca.ulaval.glo4003.trotti.domain.station.exception.StationNotFoundException;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.Password;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.AccountNotActivatedException;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StartRideUseCaseTest {

    private StartRideUseCase startRideUseCase;
    private UserRepository userRepository;
    private PassRepository passRepository;
    private StationRepository stationRepository;
    private ScooterRepository scooterRepository;
    private RideRepository rideRepository;
    private RequestRideCodeUseCase requestRideCodeUseCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passRepository = mock(PassRepository.class);
        stationRepository = mock(StationRepository.class);
        scooterRepository = mock(ScooterRepository.class);
        rideRepository = mock(RideRepository.class);
        requestRideCodeUseCase = mock(RequestRideCodeUseCase.class);

        startRideUseCase = new StartRideUseCase(
            userRepository, passRepository, stationRepository,
            scooterRepository, rideRepository, requestRideCodeUseCase
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
            "johdo123"
        );
        user.activate();

        Station station = new Station("station-1", "Campus", 20);
        Scooter scooter = new Scooter("scooter-1", 80.0, "station-1", 5);
        RideCode rideCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passRepository.hasActivePassForUser(user.getId(), LocalDate.now())).thenReturn(true);
        when(requestRideCodeUseCase.getUserCode(email)).thenReturn(rideCode);
        when(stationRepository.findById("station-1")).thenReturn(Optional.of(station));
        when(scooterRepository.findByStationIdAndSlotNumber("station-1", 5)).thenReturn(Optional.of(scooter));

        RideStartResult result = startRideUseCase.execute(email, "station-1", 5, "ABC123");

        assertNotNull(result);
        assertEquals(80.0, result.getBatteryLevel());
        verify(scooterRepository).save(scooter);
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
            "johdo123"
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

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
            "johdo123"
        );
        user.activate();

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passRepository.hasActivePassForUser(user.getId(), LocalDate.now())).thenReturn(false);

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
            "johdo123"
        );
        user.activate();

        RideCode rideCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passRepository.hasActivePassForUser(user.getId(), LocalDate.now())).thenReturn(true);
        when(requestRideCodeUseCase.getUserCode(email)).thenReturn(rideCode);

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
            "johdo123"
        );
        user.activate();

        RideCode expiredCode = new RideCode("ABC123", LocalDateTime.now().minusMinutes(1));

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passRepository.hasActivePassForUser(user.getId(), LocalDate.now())).thenReturn(true);
        when(requestRideCodeUseCase.getUserCode(email)).thenReturn(expiredCode);

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
            "johdo123"
        );
        user.activate();

        RideCode rideCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passRepository.hasActivePassForUser(user.getId(), LocalDate.now())).thenReturn(true);
        when(requestRideCodeUseCase.getUserCode(email)).thenReturn(rideCode);
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
            "johdo123"
        );
        user.activate();

        Station station = new Station("station-1", "Campus", 20);
        RideCode rideCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passRepository.hasActivePassForUser(user.getId(), LocalDate.now())).thenReturn(true);
        when(requestRideCodeUseCase.getUserCode(email)).thenReturn(rideCode);
        when(stationRepository.findById("station-1")).thenReturn(Optional.of(station));
        when(scooterRepository.findByStationIdAndSlotNumber("station-1", 5)).thenReturn(Optional.empty());

        assertThrows(SlotNotAvailableException.class, () ->
            startRideUseCase.execute(email, "station-1", 5, "ABC123")
        );
    }

    @Test
    void givenScooterWithLowBattery_whenStartingRide_thenThrowsSlotNotAvailableException() {
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123"
        );
        user.activate();

        Station station = new Station("station-1", "Campus", 20);
        Scooter scooter = new Scooter("scooter-1", 10.0, "station-1", 5);
        RideCode rideCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passRepository.hasActivePassForUser(user.getId(), LocalDate.now())).thenReturn(true);
        when(requestRideCodeUseCase.getUserCode(email)).thenReturn(rideCode);
        when(stationRepository.findById("station-1")).thenReturn(Optional.of(station));
        when(scooterRepository.findByStationIdAndSlotNumber("station-1", 5)).thenReturn(Optional.of(scooter));

        assertThrows(SlotNotAvailableException.class, () ->
            startRideUseCase.execute(email, "station-1", 5, "ABC123")
        );
    }
}

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
        // Arrange
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "M",
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

        // Act
        RideStartResult result = startRideUseCase.execute(email, "station-1", 5, "ABC123");

        // Assert
        assertNotNull(result);
        assertEquals(80.0, result.getBatteryLevel());
        verify(scooterRepository).save(scooter);
        verify(rideRepository).save(any(Ride.class));
    }

    @Test
    void givenNonExistentUser_whenStartingRide_thenThrowsUserNotFoundException() {
        // Arrange
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
            startRideUseCase.execute("test@example.com", "station-1", 5, "ABC123")
        );
    }

    @Test
    void givenNonActivatedUser_whenStartingRide_thenThrowsAccountNotActivatedException() {
        // Arrange
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "M",
            "johdo123"
        );
        // User not activated

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(AccountNotActivatedException.class, () ->
            startRideUseCase.execute(email, "station-1", 5, "ABC123")
        );
    }

    @Test
    void givenUserWithoutActivePass_whenStartingRide_thenThrowsNoActivePassException() {
        // Arrange
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "M",
            "johdo123"
        );
        user.activate();

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passRepository.hasActivePassForUser(user.getId(), LocalDate.now())).thenReturn(false);

        // Act & Assert
        assertThrows(NoActivePassException.class, () ->
            startRideUseCase.execute(email, "station-1", 5, "ABC123")
        );
    }

    @Test
    void givenInvalidRideCode_whenStartingRide_thenThrowsInvalidRideCodeException() {
        // Arrange
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "M",
            "johdo123"
        );
        user.activate();

        RideCode rideCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passRepository.hasActivePassForUser(user.getId(), LocalDate.now())).thenReturn(true);
        when(requestRideCodeUseCase.getUserCode(email)).thenReturn(rideCode);

        // Act & Assert - Wrong code provided
        assertThrows(InvalidRideCodeException.class, () ->
            startRideUseCase.execute(email, "station-1", 5, "WRONG123")
        );
    }

    @Test
    void givenExpiredRideCode_whenStartingRide_thenThrowsExpiredRideCodeException() {
        // Arrange
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "M",
            "johdo123"
        );
        user.activate();

        RideCode expiredCode = new RideCode("ABC123", LocalDateTime.now().minusMinutes(1));

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passRepository.hasActivePassForUser(user.getId(), LocalDate.now())).thenReturn(true);
        when(requestRideCodeUseCase.getUserCode(email)).thenReturn(expiredCode);

        // Act & Assert
        assertThrows(ExpiredRideCodeException.class, () ->
            startRideUseCase.execute(email, "station-1", 5, "ABC123")
        );
    }

    @Test
    void givenNonExistentStation_whenStartingRide_thenThrowsStationNotFoundException() {
        // Arrange
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "M",
            "johdo123"
        );
        user.activate();

        RideCode rideCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passRepository.hasActivePassForUser(user.getId(), LocalDate.now())).thenReturn(true);
        when(requestRideCodeUseCase.getUserCode(email)).thenReturn(rideCode);
        when(stationRepository.findById("station-1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(StationNotFoundException.class, () ->
            startRideUseCase.execute(email, "station-1", 5, "ABC123")
        );
    }

    @Test
    void givenEmptySlot_whenStartingRide_thenThrowsSlotNotAvailableException() {
        // Arrange
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "M",
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

        // Act & Assert
        assertThrows(SlotNotAvailableException.class, () ->
            startRideUseCase.execute(email, "station-1", 5, "ABC123")
        );
    }

    @Test
    void givenScooterWithLowBattery_whenStartingRide_thenThrowsSlotNotAvailableException() {
        // Arrange
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "M",
            "johdo123"
        );
        user.activate();

        Station station = new Station("station-1", "Campus", 20);
        Scooter scooter = new Scooter("scooter-1", 10.0, "station-1", 5); // Low battery
        RideCode rideCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passRepository.hasActivePassForUser(user.getId(), LocalDate.now())).thenReturn(true);
        when(requestRideCodeUseCase.getUserCode(email)).thenReturn(rideCode);
        when(stationRepository.findById("station-1")).thenReturn(Optional.of(station));
        when(scooterRepository.findByStationIdAndSlotNumber("station-1", 5)).thenReturn(Optional.of(scooter));

        // Act & Assert
        assertThrows(SlotNotAvailableException.class, () ->
            startRideUseCase.execute(email, "station-1", 5, "ABC123")
        );
    }
}

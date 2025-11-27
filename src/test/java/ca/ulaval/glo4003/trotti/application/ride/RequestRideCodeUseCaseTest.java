package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.domain.pass.Pass;
import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.pass.PricingPolicy;
import ca.ulaval.glo4003.trotti.domain.pass.Session;
import ca.ulaval.glo4003.trotti.domain.pass.exception.NoActivePassException;
import ca.ulaval.glo4003.trotti.domain.ride.RideCode;
import ca.ulaval.glo4003.trotti.domain.ride.RideCodeGenerator;
import ca.ulaval.glo4003.trotti.domain.ride.RideCodeRepository;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.Password;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.AccountNotActivatedException;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;
import ca.ulaval.glo4003.trotti.infrastructure.time.FixedClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RequestRideCodeUseCaseTest {

    private RequestRideCodeUseCase requestRideCodeUseCase;
    private UserRepository userRepository;
    private PassRepository passRepository;
    private RideCodeGenerator rideCodeGenerator;
    private RideCodeRepository rideCodeRepository;
    private EmailService emailService;
    private Clock clock;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passRepository = mock(PassRepository.class);
        rideCodeGenerator = mock(RideCodeGenerator.class);
        rideCodeRepository = mock(RideCodeRepository.class);
        emailService = mock(EmailService.class);
        clock = new FixedClock(LocalDate.of(2025, 11, 16));

        requestRideCodeUseCase = new RequestRideCodeUseCase(
            userRepository, passRepository, rideCodeGenerator,
            rideCodeRepository, emailService, clock
        );
    }

    @Test
    void givenActivatedUserWithActivePass_whenExecuting_thenGeneratesAndSavesRideCode() {
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

        RideCode expectedCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));

        Pass activePass = Pass.purchase(user.getId(), Session.A2025, 0, false, new PricingPolicy());
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passRepository.findByUserId(user.getId())).thenReturn(List.of(activePass));
        when(rideCodeGenerator.generate()).thenReturn(expectedCode);

        RideCode result = requestRideCodeUseCase.execute(email);

        assertEquals(expectedCode, result);

        verify(rideCodeRepository).save(email, expectedCode);
        verify(emailService, times(2)).send(any()); // 1 for activation, 1 for ride code
    }

    @Test
    void givenNonExistentUser_whenExecuting_thenThrowsUserNotFoundException() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
            requestRideCodeUseCase.execute(email)
        );

        verify(rideCodeGenerator, never()).generate();
        verify(rideCodeRepository, never()).save(anyString(), any());
    }

    @Test
    void givenNonActivatedUser_whenExecuting_thenThrowsAccountNotActivatedException() {
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

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passRepository.findByUserId(user.getId())).thenReturn(List.of());

        assertThrows(AccountNotActivatedException.class, () ->
            requestRideCodeUseCase.execute(email)
        );

        verify(rideCodeGenerator, never()).generate();
    }

    @Test
    void givenUserWithoutActivePass_whenExecuting_thenThrowsNoActivePassException() {
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

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passRepository.findByUserId(user.getId())).thenReturn(List.of());

        assertThrows(NoActivePassException.class, () ->
            requestRideCodeUseCase.execute(email)
        );

        verify(rideCodeGenerator, never()).generate();
        verify(rideCodeRepository, never()).save(anyString(), any());
    }

    @Test
    void givenUserEmail_whenGettingUserCode_thenReturnsCodeFromRepository() {
        String email = "test@example.com";
        RideCode expectedCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));

        when(rideCodeRepository.findByUserEmail(email)).thenReturn(Optional.of(expectedCode));

        RideCode result = requestRideCodeUseCase.getUserCode(email);

        assertEquals(expectedCode, result);
        verify(rideCodeRepository).findByUserEmail(email);
    }

    @Test
    void givenUserEmailWithNoCode_whenGettingUserCode_thenReturnsNull() {
        String email = "test@example.com";

        when(rideCodeRepository.findByUserEmail(email)).thenReturn(Optional.empty());

        RideCode result = requestRideCodeUseCase.getUserCode(email);

        assertNull(result);
    }
}

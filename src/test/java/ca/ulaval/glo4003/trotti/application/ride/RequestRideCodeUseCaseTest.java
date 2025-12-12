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
    private RideCodeGenerator rideCodeGenerator;
    private EmailService emailService;
    private Clock clock;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        rideCodeGenerator = mock(RideCodeGenerator.class);
        emailService = mock(EmailService.class);
        clock = new FixedClock(LocalDate.of(2025, 11, 16));

        requestRideCodeUseCase = new RequestRideCodeUseCase(
            userRepository, rideCodeGenerator, emailService, clock
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

        Pass activePass = Pass.purchase(user.getId(), Session.generate(2025, "Automne"), 0, false, new PricingPolicy());
        user.addPass(activePass);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideCodeGenerator.generate()).thenReturn(expectedCode);

        RideCode result = requestRideCodeUseCase.execute(email);

        assertEquals(expectedCode, result);

        verify(userRepository).save(user);
        verify(emailService, times(2)).send(any());
    }

    @Test
    void givenNonExistentUser_whenExecuting_thenThrowsUserNotFoundException() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
            requestRideCodeUseCase.execute(email)
        );

        verify(rideCodeGenerator, never()).generate();
        verify(userRepository, never()).save(any());
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

        assertThrows(NoActivePassException.class, () ->
            requestRideCodeUseCase.execute(email)
        );

        verify(rideCodeGenerator, never()).generate();
        verify(userRepository, never()).save(any());
    }

    @Test
    void givenUserEmail_whenGettingUserCode_thenReturnsCodeFromRepository() {
        String email = "test@example.com";
        RideCode expectedCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));

        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123",
            Role.USER
        );
        user.setCurrentRideCode(expectedCode);

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        RideCode result = requestRideCodeUseCase.getUserCode(email);

        assertEquals(expectedCode, result);
        verify(userRepository).findByEmail(any(Email.class));
    }

    @Test
    void givenUserEmailWithNoCode_whenGettingUserCode_thenReturnsNull() {
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

        RideCode result = requestRideCodeUseCase.getUserCode(email);

        assertNull(result);
    }
}

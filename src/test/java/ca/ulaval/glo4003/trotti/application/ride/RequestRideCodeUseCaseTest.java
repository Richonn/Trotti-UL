package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.pass.exception.NoActivePassException;
import ca.ulaval.glo4003.trotti.domain.ride.RideCode;
import ca.ulaval.glo4003.trotti.domain.ride.RideCodeGenerator;
import ca.ulaval.glo4003.trotti.domain.ride.RideCodeRepository;
import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.Password;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.AccountNotActivatedException;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestRideCodeUseCaseTest {

    private RequestRideCodeUseCase requestRideCodeUseCase;
    private UserRepository userRepository;
    private PassRepository passRepository;
    private RideCodeGenerator rideCodeGenerator;
    private RideCodeRepository rideCodeRepository;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passRepository = mock(PassRepository.class);
        rideCodeGenerator = mock(RideCodeGenerator.class);
        rideCodeRepository = mock(RideCodeRepository.class);
        emailService = mock(EmailService.class);

        requestRideCodeUseCase = new RequestRideCodeUseCase(
            userRepository, passRepository, rideCodeGenerator,
            rideCodeRepository, emailService
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
            "M",
            "johdo123"
        );
        user.activate();

        RideCode expectedCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passRepository.hasActivePassForUser(user.getId(), LocalDate.now())).thenReturn(true);
        when(rideCodeGenerator.generate()).thenReturn(expectedCode);

        RideCode result = requestRideCodeUseCase.execute(email);

        assertEquals(expectedCode, result);

        verify(rideCodeRepository).save(email, expectedCode);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService).sendEmail(emailCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture());

        assertEquals(email, emailCaptor.getValue());
        assertTrue(subjectCaptor.getValue().contains("déverrouillage"));
        assertTrue(bodyCaptor.getValue().contains("ABC123"));
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
            "M",
            "johdo123"
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        assertThrows(AccountNotActivatedException.class, () ->
            requestRideCodeUseCase.execute(email)
        );

        verify(passRepository, never()).hasActivePassForUser(anyString(), any(LocalDate.class));
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
            "M",
            "johdo123"
        );
        user.activate();

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passRepository.hasActivePassForUser(user.getId(), LocalDate.now())).thenReturn(false);

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

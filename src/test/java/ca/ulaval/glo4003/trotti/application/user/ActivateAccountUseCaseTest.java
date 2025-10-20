package ca.ulaval.glo4003.trotti.application.user;

import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.user.*;
import ca.ulaval.glo4003.trotti.domain.user.exception.AccountAlreadyActivatedException;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActivateAccountUseCaseTest {

    private ActivateAccountUseCase activateAccountUseCase;
    private UserRepository userRepository;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        emailService = mock(EmailService.class);
        activateAccountUseCase = new ActivateAccountUseCase(userRepository, emailService);
    }

    @Test
    void givenValidUser_whenExecuting_thenActivatesAccountAndSendsEmail() {
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

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        // Act
        activateAccountUseCase.execute(email);

        // Assert
        assertTrue(user.isActivated());
        verify(userRepository).save(user);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService).sendEmail(emailCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture());

        assertEquals(email, emailCaptor.getValue());
        assertTrue(subjectCaptor.getValue().contains("TrottiUL"));
        assertTrue(bodyCaptor.getValue().contains("John Doe"));
    }

    @Test
    void givenNonExistentUser_whenExecuting_thenThrowsUserNotFoundException() {
        // Arrange
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
            activateAccountUseCase.execute(email)
        );

        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void givenAlreadyActivatedUser_whenExecuting_thenThrowsAccountAlreadyActivatedException() {
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

        // Act & Assert
        assertThrows(AccountAlreadyActivatedException.class, () ->
            activateAccountUseCase.execute(email)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenInvalidEmail_whenExecuting_thenThrowsIllegalArgumentException() {
        // Arrange
        String invalidEmail = "not-an-email";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            activateAccountUseCase.execute(invalidEmail)
        );

        verify(userRepository, never()).findByEmail(any());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}

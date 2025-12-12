package ca.ulaval.glo4003.trotti.application.user;

import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.user.*;
import ca.ulaval.glo4003.trotti.domain.user.exception.AccountAlreadyActivatedException;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

        activateAccountUseCase.execute(email);

        assertTrue(user.isActivated());
        verify(userRepository).save(user);
        verify(emailService).send(any());
    }

    @Test
    void givenNonExistentUser_whenExecuting_thenThrowsUserNotFoundException() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
            activateAccountUseCase.execute(email)
        );

        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).send(any());
    }

    @Test
    void givenAlreadyActivatedUser_whenExecuting_thenThrowsAccountAlreadyActivatedException() {
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

        assertThrows(AccountAlreadyActivatedException.class, () ->
            activateAccountUseCase.execute(email)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenInvalidEmail_whenExecuting_thenThrowsIllegalArgumentException() {
        String invalidEmail = "not-an-email";

        assertThrows(IllegalArgumentException.class, () ->
            activateAccountUseCase.execute(invalidEmail)
        );

        verify(userRepository, never()).findByEmail(any());
        verify(emailService, never()).send(any());
    }
}

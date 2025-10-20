package ca.ulaval.glo4003.trotti.application.user;

import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterUserUseCaseTest {

    private RegisterUserUseCase registerUserUseCase;
    private UserRepository userRepository;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        emailService = mock(EmailService.class);
        registerUserUseCase = new RegisterUserUseCase(userRepository, emailService);
    }

    @Test
    void givenValidData_whenExecuting_thenCreatesAndSavesUser() {
        String email = "test@example.com";
        String password = "ValidPass123!";
        String name = "John Doe";
        String dateOfBirth = "2000-01-15";
        String gender = "M";
        String idul = "johdo123";

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);

        String userId = registerUserUseCase.execute(email, password, name, dateOfBirth, gender, idul);

        assertNotNull(userId);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(email, savedUser.getEmail().getValue());
        assertEquals(name, savedUser.getName());
        assertFalse(savedUser.isActivated());
    }

    @Test
    void givenExistingEmail_whenExecuting_thenThrowsUserAlreadyExistsException() {
        String email = "existing@example.com";
        String password = "ValidPass123!";
        String name = "John Doe";
        String dateOfBirth = "2000-01-15";
        String gender = "M";
        String idul = "johdo123";

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () ->
            registerUserUseCase.execute(email, password, name, dateOfBirth, gender, idul)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenInvalidEmail_whenExecuting_thenThrowsIllegalArgumentException() {
        String email = "invalid-email";
        String password = "ValidPass123!";
        String name = "John Doe";
        String dateOfBirth = "2000-01-15";
        String gender = "M";
        String idul = "johdo123";

        assertThrows(IllegalArgumentException.class, () ->
            registerUserUseCase.execute(email, password, name, dateOfBirth, gender, idul)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenInvalidPassword_whenExecuting_thenThrowsIllegalArgumentException() {
        String email = "test@example.com";
        String password = "weak";
        String name = "John Doe";
        String dateOfBirth = "2000-01-15";
        String gender = "M";
        String idul = "johdo123";

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
            registerUserUseCase.execute(email, password, name, dateOfBirth, gender, idul)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenValidData_whenExecuting_thenChecksIfEmailExists() {
        String email = "test@example.com";
        String password = "ValidPass123!";
        String name = "John Doe";
        String dateOfBirth = "2000-01-15";
        String gender = "M";
        String idul = "johdo123";

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);

        registerUserUseCase.execute(email, password, name, dateOfBirth, gender, idul);

        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(userRepository).existsByEmail(emailCaptor.capture());
        assertEquals(email, emailCaptor.getValue().getValue());
    }
}

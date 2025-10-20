package ca.ulaval.glo4003.trotti.application.user;

import ca.ulaval.glo4003.trotti.domain.security.TokenGenerator;
import ca.ulaval.glo4003.trotti.domain.user.*;
import ca.ulaval.glo4003.trotti.domain.user.exception.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginUserUseCaseTest {

    private LoginUserUseCase loginUserUseCase;
    private UserRepository userRepository;
    private TokenGenerator tokenGenerator;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        tokenGenerator = mock(TokenGenerator.class);
        loginUserUseCase = new LoginUserUseCase(userRepository, tokenGenerator);
    }

    @Test
    void givenValidCredentials_whenExecuting_thenReturnsToken() {
        // Arrange
        String email = "test@example.com";
        String password = "ValidPass123!";
        String expectedToken = "jwt-token-123";

        User user = User.register(
            new Email(email),
            new Password(password),
            "John Doe",
            "2000-01-15",
            "M",
            "johdo123"
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(tokenGenerator.generateToken(email)).thenReturn(expectedToken);

        // Act
        String token = loginUserUseCase.execute(email, password);

        // Assert
        assertEquals(expectedToken, token);
        verify(tokenGenerator).generateToken(email);
    }

    @Test
    void givenNonExistentEmail_whenExecuting_thenThrowsInvalidCredentialsException() {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "ValidPass123!";

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () ->
            loginUserUseCase.execute(email, password)
        );

        verify(tokenGenerator, never()).generateToken(anyString());
    }

    @Test
    void givenWrongPassword_whenExecuting_thenThrowsInvalidCredentialsException() {
        // Arrange
        String email = "test@example.com";
        String correctPassword = "ValidPass123!";
        String wrongPassword = "WrongPass456!";

        User user = User.register(
            new Email(email),
            new Password(correctPassword),
            "John Doe",
            "2000-01-15",
            "M",
            "johdo123"
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () ->
            loginUserUseCase.execute(email, wrongPassword)
        );

        verify(tokenGenerator, never()).generateToken(anyString());
    }

    @Test
    void givenInvalidEmailFormat_whenExecuting_thenThrowsIllegalArgumentException() {
        // Arrange
        String invalidEmail = "not-an-email";
        String password = "ValidPass123!";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            loginUserUseCase.execute(invalidEmail, password)
        );

        verify(userRepository, never()).findByEmail(any());
        verify(tokenGenerator, never()).generateToken(anyString());
    }

    @Test
    void givenNonActivatedUser_whenExecuting_thenStillGeneratesToken() {
        // Arrange - User can login even if not activated
        String email = "test@example.com";
        String password = "ValidPass123!";
        String expectedToken = "jwt-token-123";

        User user = User.register(
            new Email(email),
            new Password(password),
            "John Doe",
            "2000-01-15",
            "M",
            "johdo123"
        );
        // User is not activated

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(tokenGenerator.generateToken(email)).thenReturn(expectedToken);

        // Act
        String token = loginUserUseCase.execute(email, password);

        // Assert
        assertEquals(expectedToken, token);
        assertFalse(user.isActivated());
    }
}

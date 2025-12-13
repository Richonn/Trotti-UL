package ca.ulaval.glo4003.trotti.application.billing;

import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.Password;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateAutoPayPreferenceUseCaseTest {
    @Mock
    private UserRepository userRepository;

    private UpdateAutoPayPreferenceUseCase useCase;
    private String userEmail;
    private User user;

    @BeforeEach
    void setUp() {
        useCase = new UpdateAutoPayPreferenceUseCase(userRepository);
        userEmail = "test@example.com";
        user = User.register(
                new Email(userEmail),
                new Password("ValidPass123!"),
                "Test User",
                "2000-01-01",
                "H",
                "AAAA00000001",
                Role.USER
        );
    }

    @Test
    void givenValidUser_whenExecuteWithTrue_thenEnablesAutoPay() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        useCase.execute(userEmail, true);

        verify(userRepository).findByEmail(any(Email.class));
    }

    @Test
    void givenValidUser_whenExecuteWithFalse_thenDisablesAutoPay() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        useCase.execute(userEmail, false);

        verify(userRepository).findByEmail(any(Email.class));
    }

    @Test
    void givenUserNotFound_whenExecute_thenThrowsUserNotFoundException() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userEmail, true))
                .isInstanceOf(UserNotFoundException.class);
    }
}

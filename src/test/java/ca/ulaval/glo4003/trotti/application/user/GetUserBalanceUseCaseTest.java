package ca.ulaval.glo4003.trotti.application.user;

import ca.ulaval.glo4003.trotti.domain.pass.Money;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserBalanceUseCaseTest {
    @Mock
    private UserRepository userRepository;

    private GetUserBalanceUseCase useCase;
    private String userEmail;
    private User user;

    @BeforeEach
    void setUp() {
        useCase = new GetUserBalanceUseCase(userRepository);
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
    void givenUserWithZeroBalance_whenExecute_thenReturnsZero() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        BigDecimal balance = useCase.execute(userEmail);

        assertThat(balance).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void givenUserWithBalance_whenExecute_thenReturnsBalance() {
        user.addToMonthlyBalance(new Money(new BigDecimal("25.50")), LocalDate.now());
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        BigDecimal balance = useCase.execute(userEmail);

        assertThat(balance).isEqualByComparingTo(new BigDecimal("25.50"));
    }

    @Test
    void givenUserNotFound_whenExecute_thenThrowsUserNotFoundException() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userEmail))
                .isInstanceOf(UserNotFoundException.class);
    }
}

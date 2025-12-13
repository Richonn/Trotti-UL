package ca.ulaval.glo4003.trotti.application.pass;

import ca.ulaval.glo4003.trotti.application.pass.dto.PassDTO;
import ca.ulaval.glo4003.trotti.domain.pass.Pass;
import ca.ulaval.glo4003.trotti.domain.pass.PricingPolicy;
import ca.ulaval.glo4003.trotti.domain.pass.Session;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserPassesUseCaseTest {
    @Mock
    private UserRepository userRepository;

    private GetUserPassesUseCase useCase;
    private String userEmail;
    private User user;

    @BeforeEach
    void setUp() {
        useCase = new GetUserPassesUseCase(userRepository);
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
    void givenUserWithNoPasses_whenExecute_thenReturnsEmptyList() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        List<PassDTO> passes = useCase.execute(userEmail);

        assertThat(passes).isEmpty();
    }

    @Test
    void givenUserWithOnePasses_whenExecute_thenReturnsOnePass() {
        Pass pass = Pass.purchase(user.getId(), Session.getInstance("A2025"), 0, false, new PricingPolicy());
        user.addPass(pass);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        List<PassDTO> passes = useCase.execute(userEmail);

        assertThat(passes).hasSize(1);
        assertThat(passes.get(0).getSession()).isEqualTo("A2025");
    }

    @Test
    void givenUserWithMultiplePasses_whenExecute_thenReturnsAllPasses() {
        Pass pass1 = Pass.purchase(user.getId(), Session.getInstance("A2025"), 0, false, new PricingPolicy());
        Pass pass2 = Pass.purchase(user.getId(), Session.getInstance("H2025"), 10, true, new PricingPolicy());
        user.addPass(pass1);
        user.addPass(pass2);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        List<PassDTO> passes = useCase.execute(userEmail);

        assertThat(passes).hasSize(2);
    }

    @Test
    void givenUserNotFound_whenExecute_thenThrowsUserNotFoundException() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userEmail))
                .isInstanceOf(UserNotFoundException.class);
    }
}

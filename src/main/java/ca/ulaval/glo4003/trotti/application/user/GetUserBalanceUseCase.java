package ca.ulaval.glo4003.trotti.application.user;

import java.math.BigDecimal;

import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;

public class GetUserBalanceUseCase {
    private final UserRepository userRepository;

    public GetUserBalanceUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public BigDecimal execute(String email) {
        User user = this.userRepository.findByEmail(new Email(email)).orElseThrow(UserNotFoundException::new);

        return user.getMonthlyBalance().getAmountValue();
    }
}

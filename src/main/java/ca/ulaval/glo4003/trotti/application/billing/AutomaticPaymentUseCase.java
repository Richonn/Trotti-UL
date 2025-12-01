package ca.ulaval.glo4003.trotti.application.billing;

import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;

import java.math.BigDecimal;
import java.util.List;

public class AutomaticPaymentUseCase {
    private final UserRepository userRepository;

    public AutomaticPaymentUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute() {
        List<User> users = userRepository.findAll().stream()
                .filter(User::isAutoPayEnabled)
                .filter(user -> user.getMonthlyBalance().getAmount().isZero())
                .toList();
    }
}

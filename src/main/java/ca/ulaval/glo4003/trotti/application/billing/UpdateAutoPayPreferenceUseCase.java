package ca.ulaval.glo4003.trotti.application.billing;

import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;

public class UpdateAutoPayPreferenceUseCase {
    private final UserRepository userRepository;
    public UpdateAutoPayPreferenceUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(String userEmail, boolean enabled) {
        Email email = new Email(userEmail);
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        user.setAutoPayEnabled(enabled);
    }
}

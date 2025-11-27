package ca.ulaval.glo4003.trotti.application.user;

import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;

public class ActivateAccountUseCase {
    private final UserRepository userRepository;
    private final EmailService emailService;

    public ActivateAccountUseCase(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public void execute(String email) {
        Email userEmail = new Email(email);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(UserNotFoundException::new);

        user.activate(emailService);
        userRepository.save(user);
    }
}

package ca.ulaval.glo4003.trotti.application.user;

import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.user.*;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserAlreadyExistsException;

public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final EmailService emailService;

    public RegisterUserUseCase(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public String execute(String email, String password, String name, String dateOfBirth, String gender, String idul) {
        Email userEmail = new Email(email);

        if (userRepository.existsByEmail(userEmail)) {
            throw new UserAlreadyExistsException();
        }

        User user = User.register(userEmail, new Password(password), name, dateOfBirth, gender, idul);
        userRepository.save(user);

        return user.getId();
    }
}

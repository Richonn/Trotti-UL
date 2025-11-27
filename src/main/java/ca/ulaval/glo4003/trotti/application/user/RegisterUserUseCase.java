package ca.ulaval.glo4003.trotti.application.user;

import ca.ulaval.glo4003.trotti.application.user.dto.UserDTO;
import ca.ulaval.glo4003.trotti.application.user.mapper.UserMapper;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.user.*;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserAlreadyExistsException;

public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final Clock clock;

    public RegisterUserUseCase(UserRepository userRepository, EmailService emailService, Clock clock) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.clock = clock;
    }

    public UserDTO execute(String email, String password, String name, String dateOfBirth, String gender, String idul, Role role) {
        Email userEmail = new Email(email);

        if (userRepository.existsByEmail(userEmail)) {
            throw new UserAlreadyExistsException();
        }

        User user = User.register(userEmail, new Password(password), name, dateOfBirth, gender, idul, role);
        userRepository.save(user);

        return UserMapper.toDTO(user, clock);
    }
}

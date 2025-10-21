package ca.ulaval.glo4003.trotti.application.user;

import ca.ulaval.glo4003.trotti.domain.security.TokenGenerator;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.InvalidCredentialsException;

public class LoginUserUseCase {
    private final UserRepository userRepository;
    private final TokenGenerator tokenGenerator;

    public LoginUserUseCase(UserRepository userRepository, TokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.tokenGenerator = tokenGenerator;
    }

    public String execute(String email, String password) {
        Email userEmail = new Email(email);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.getPassword().matches(password)) {
            throw new InvalidCredentialsException("Invalid password");
        }

        return tokenGenerator.generateToken(email);
    }
}

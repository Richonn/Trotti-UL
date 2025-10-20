package ca.ulaval.glo4003.trotti.application.user;

import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.trotti.infrastructure.security.JwtTokenService;

public class LoginUserUseCase {
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    public LoginUserUseCase(UserRepository userRepository, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }

    public String execute(String email, String password) {
        Email userEmail = new Email(email);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.getPassword().matches(password)) {
            throw new InvalidCredentialsException("Invalid password");
        }

        return jwtTokenService.generateToken(email);
    }
}

package ca.ulaval.glo4003.trotti.application.pass;

import ca.ulaval.glo4003.trotti.domain.pass.Pass;
import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;

import java.util.List;

public class GetUserPassesUseCase {
    private final UserRepository userRepository;
    private final PassRepository passRepository;

    public GetUserPassesUseCase(UserRepository userRepository, PassRepository passRepository) {
        this.userRepository = userRepository;
        this.passRepository = passRepository;
    }

    public List<Pass> execute(String email) {
        Email userEmail = new Email(email);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(UserNotFoundException::new);

        return passRepository.findByUserId(user.getId());
    }
}

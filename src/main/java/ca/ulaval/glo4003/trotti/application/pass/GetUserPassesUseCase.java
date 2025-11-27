package ca.ulaval.glo4003.trotti.application.pass;

import ca.ulaval.glo4003.trotti.application.pass.dto.PassDTO;
import ca.ulaval.glo4003.trotti.application.pass.mapper.PassMapper;
import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

public class GetUserPassesUseCase {
    private final UserRepository userRepository;
    private final PassRepository passRepository;

    public GetUserPassesUseCase(UserRepository userRepository, PassRepository passRepository) {
        this.userRepository = userRepository;
        this.passRepository = passRepository;
    }

    public List<PassDTO> execute(String email) {
        Email userEmail = new Email(email);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(UserNotFoundException::new);

        return passRepository.findByUserId(user.getId()).stream()
            .map(PassMapper::toDTO)
            .collect(Collectors.toList());
    }
}

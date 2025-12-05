package ca.ulaval.glo4003.trotti.application.pass;

import ca.ulaval.glo4003.trotti.application.pass.dto.PassDTO;
import ca.ulaval.glo4003.trotti.application.pass.mapper.PassMapper;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

public class GetUserPassesUseCase {
    private final UserRepository userRepository;

    public GetUserPassesUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<PassDTO> execute(String email) {
        Email userEmail = new Email(email);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(UserNotFoundException::new);

        return user.getPasses().stream()
            .map(PassMapper::toDTO)
            .collect(Collectors.toList());
    }
}

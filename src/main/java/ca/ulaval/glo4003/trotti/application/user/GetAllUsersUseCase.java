package ca.ulaval.glo4003.trotti.application.user;

import ca.ulaval.glo4003.trotti.application.user.dto.UserDTO;
import ca.ulaval.glo4003.trotti.application.user.mapper.UserMapper;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

public class GetAllUsersUseCase {
    private final UserRepository userRepository;
    private final Clock clock;

    public GetAllUsersUseCase(UserRepository userRepository, Clock clock) {
        this.userRepository = userRepository;
        this.clock = clock;
    }

    public List<UserDTO> execute() {
        return userRepository.findAll().stream()
            .map(user -> UserMapper.toDTO(user, clock))
            .collect(Collectors.toList());
    }
}

package ca.ulaval.glo4003.trotti.application.user.mapper;

import ca.ulaval.glo4003.trotti.application.user.dto.UserDTO;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.user.User;

public class UserMapper {

    public static UserDTO toDTO(User user, Clock clock) {
        return new UserDTO(
            user.getEmail().getValue(),
            user.getName(),
            user.getDateOfBirth().toString(),
            user.getGender().getCode(),
            user.getIdul().getValue(),
            user.isActivated(),
            user.getAge(clock)
        );
    }
}

package ca.ulaval.glo4003.trotti.interfaces.rest.user.mapper;

import ca.ulaval.glo4003.trotti.application.user.dto.UserDTO;
import ca.ulaval.glo4003.trotti.interfaces.rest.user.model.User;

import java.util.List;

public class UserResponseMapper {

    public static User toModel(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setDateOfBirth(userDTO.getDateOfBirth());
        user.setGender(userDTO.getGender());
        user.setIdul(userDTO.getIdul());
        if (userDTO.isActivated()) {
            user.activate();
        }
        return user;
    }

    public static List<User> toModelList(List<UserDTO> userDTOs) {
        return userDTOs.stream()
                .map(UserResponseMapper::toModel)
                .toList();
    }
}

package ca.ulaval.glo4003.trotti.interfaces.rest.user.mapper;

import ca.ulaval.glo4003.trotti.application.user.dto.UserDTO;
import ca.ulaval.glo4003.trotti.interfaces.rest.user.response.UserResponse;

import java.util.List;

public class UserResponseMapper {

    public static UserResponse toModel(UserDTO userDTO) {
        UserResponse user = new UserResponse();
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

    public static List<UserResponse> toModelList(List<UserDTO> userDTOs) {
        return userDTOs.stream()
                .map(UserResponseMapper::toModel)
                .toList();
    }
}

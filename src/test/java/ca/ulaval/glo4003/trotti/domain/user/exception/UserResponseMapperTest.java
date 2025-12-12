package ca.ulaval.glo4003.trotti.domain.user.exception;

import ca.ulaval.glo4003.trotti.application.user.dto.UserDTO;
import ca.ulaval.glo4003.trotti.interfaces.rest.user.mapper.UserResponseMapper;
import ca.ulaval.glo4003.trotti.interfaces.rest.user.response.UserResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserResponseMapperTest {

    @Test
    void givenUserDTO_whenMapped_thenAllFieldsAreMapped() {
        UserDTO dto = new UserDTO(
                "john@example.com",
                "John Doe",
                "2000-01-01",
                "MALE",
                "johndoe",
                true,
                24
        );

        UserResponse response = UserResponseMapper.toModel(dto);

        assertEquals(dto.getName(), response.getName());
        assertEquals(dto.getEmail(), response.getEmail());
        assertEquals(dto.getDateOfBirth(), response.getDateOfBirth());
        assertEquals(dto.getGender(), response.getGender());
        assertEquals(dto.getIdul(), response.getIdul());
        assertTrue(response.isActivated());
    }

    @Test
    void givenUserDTO_notActivated_whenMapped_thenResponseNotActivated() {
        UserDTO dto = new UserDTO(
                "john@example.com",
                "John Doe",
                "2000-01-01",
                "MALE",
                "johndoe",
                false,
                24
        );

        UserResponse response = UserResponseMapper.toModel(dto);

        assertFalse(response.isActivated());
    }

    @Test
    void givenListOfUserDTO_whenMapped_thenAllAreMapped() {
        UserDTO dto1 = new UserDTO("a@a.com", "Alice", "1990-01-01", "FEMALE", "alice", true, 34);
        UserDTO dto2 = new UserDTO("b@b.com", "Bob", "1985-05-05", "MALE", "bob", false, 39);

        List<UserResponse> responses = UserResponseMapper.toModelList(List.of(dto1, dto2));

        assertEquals(2, responses.size());
        assertEquals("Alice", responses.get(0).getName());
        assertEquals("Bob", responses.get(1).getName());
    }
}
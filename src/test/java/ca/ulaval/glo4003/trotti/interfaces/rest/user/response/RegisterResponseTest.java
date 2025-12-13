package ca.ulaval.glo4003.trotti.interfaces.rest.user.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterResponseTest {

    @Test
    void givenUserResponse_whenCreated_thenReturnsCorrectValues() {
        UserResponse user = new UserResponse();
        user.setName("John Doe");
        user.setDateOfBirth("2000-01-01");
        user.setGender("M");
        user.setIdul("jodoe01");
        user.setEmail("john@example.com");

        RegisterResponse response = new RegisterResponse(user);

        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getDateOfBirth()).isEqualTo("2000-01-01");
        assertThat(response.getGender()).isEqualTo("M");
        assertThat(response.getIdul()).isEqualTo("jodoe01");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void givenDifferentUser_whenCreated_thenReturnsDifferentValues() {
        UserResponse user = new UserResponse();
        user.setName("Jane Smith");
        user.setEmail("jane@test.com");

        RegisterResponse response = new RegisterResponse(user);

        assertThat(response.getName()).isEqualTo("Jane Smith");
        assertThat(response.getEmail()).isEqualTo("jane@test.com");
    }
}

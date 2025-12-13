package ca.ulaval.glo4003.trotti.interfaces.rest.user.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseTest {

    @Test
    void givenValidValues_whenSet_thenReturnsCorrectValues() {
        UserResponse response = new UserResponse();
        response.setName("John Doe");
        response.setDateOfBirth("2000-01-01");
        response.setGender("M");
        response.setIdul("jodoe01");
        response.setEmail("john@example.com");
        response.setPassword("password123");

        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getDateOfBirth()).isEqualTo("2000-01-01");
        assertThat(response.getGender()).isEqualTo("M");
        assertThat(response.getIdul()).isEqualTo("jodoe01");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getPassword()).isEqualTo("password123");
    }

    @Test
    void givenNewUser_whenNotActivated_thenReturnsFalse() {
        UserResponse response = new UserResponse();

        assertThat(response.isActivated()).isFalse();
    }

    @Test
    void givenUser_whenActivated_thenReturnsTrue() {
        UserResponse response = new UserResponse();
        response.activate();

        assertThat(response.isActivated()).isTrue();
    }

    @Test
    void givenNotActivatedUser_whenActivateAccount_thenReturnsTrue() {
        UserResponse response = new UserResponse();

        boolean result = response.activateAccount();

        assertThat(result).isTrue();
        assertThat(response.isActivated()).isTrue();
    }

    @Test
    void givenAlreadyActivatedUser_whenActivateAccount_thenReturnsFalse() {
        UserResponse response = new UserResponse();
        response.activate();

        boolean result = response.activateAccount();

        assertThat(result).isFalse();
        assertThat(response.isActivated()).isTrue();
    }
}

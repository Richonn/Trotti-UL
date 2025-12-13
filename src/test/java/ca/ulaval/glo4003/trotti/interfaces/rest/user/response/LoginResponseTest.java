package ca.ulaval.glo4003.trotti.interfaces.rest.user.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginResponseTest {

    @Test
    void givenToken_whenCreated_thenReturnsToken() {
        LoginResponse response = new LoginResponse("jwt-token-12345");

        assertThat(response.getToken()).isEqualTo("jwt-token-12345");
    }

    @Test
    void givenDifferentToken_whenCreated_thenReturnsDifferentToken() {
        LoginResponse response = new LoginResponse("another-token-67890");

        assertThat(response.getToken()).isEqualTo("another-token-67890");
    }
}

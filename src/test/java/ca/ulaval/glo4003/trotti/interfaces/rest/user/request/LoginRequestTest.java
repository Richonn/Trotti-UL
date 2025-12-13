package ca.ulaval.glo4003.trotti.interfaces.rest.user.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    @Test
    void givenValidCredentials_whenSet_thenReturnsCorrectValues() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");

        assertThat(request.getEmail()).isEqualTo("user@example.com");
        assertThat(request.getPassword()).isEqualTo("password123");
    }

    @Test
    void givenDifferentEmail_whenSet_thenReturnsEmail() {
        LoginRequest request = new LoginRequest();
        request.setEmail("another@test.com");

        assertThat(request.getEmail()).isEqualTo("another@test.com");
    }
}

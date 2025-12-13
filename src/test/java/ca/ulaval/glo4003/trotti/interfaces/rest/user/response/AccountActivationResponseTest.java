package ca.ulaval.glo4003.trotti.interfaces.rest.user.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountActivationResponseTest {

    @Test
    void givenMessage_whenCreated_thenReturnsMessage() {
        AccountActivationResponse response = new AccountActivationResponse("Account activated successfully");

        assertThat(response.getMessage()).isEqualTo("Account activated successfully");
    }

    @Test
    void givenDifferentMessage_whenCreated_thenReturnsDifferentMessage() {
        AccountActivationResponse response = new AccountActivationResponse("Activation complete");

        assertThat(response.getMessage()).isEqualTo("Activation complete");
    }
}

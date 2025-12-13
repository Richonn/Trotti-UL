package ca.ulaval.glo4003.trotti.domain.user.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountNotActivatedExceptionTest {

    @Test
    void whenCreated_thenReturnsDefaultMessage() {
        AccountNotActivatedException exception = new AccountNotActivatedException();

        assertThat(exception.getMessage()).isEqualTo("Account not activated");
    }
}

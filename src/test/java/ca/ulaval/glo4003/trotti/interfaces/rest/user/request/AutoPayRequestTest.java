package ca.ulaval.glo4003.trotti.interfaces.rest.user.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AutoPayRequestTest {

    @Test
    void givenEnabledTrue_whenSetEnabled_thenReturnsTrue() {
        AutoPayRequest request = new AutoPayRequest();
        request.setEnabled(true);

        assertThat(request.isEnabled()).isTrue();
    }

    @Test
    void givenEnabledFalse_whenSetEnabled_thenReturnsFalse() {
        AutoPayRequest request = new AutoPayRequest();
        request.setEnabled(false);

        assertThat(request.isEnabled()).isFalse();
    }
}

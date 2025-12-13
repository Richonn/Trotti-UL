package ca.ulaval.glo4003.trotti.interfaces.rest.user.response;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class GetUserBalanceResponseTest {

    @Test
    void givenBalance_whenCreated_thenReturnsBalance() {
        GetUserBalanceResponse response = new GetUserBalanceResponse(new BigDecimal("50.00"));

        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void givenZeroBalance_whenCreated_thenReturnsZero() {
        GetUserBalanceResponse response = new GetUserBalanceResponse(BigDecimal.ZERO);

        assertThat(response.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}

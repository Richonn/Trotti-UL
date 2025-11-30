package ca.ulaval.glo4003.trotti.interfaces.rest.user.dto;

import java.math.BigDecimal;

public class GetUserBalanceResponse {
    private final BigDecimal amount;

    public GetUserBalanceResponse(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}

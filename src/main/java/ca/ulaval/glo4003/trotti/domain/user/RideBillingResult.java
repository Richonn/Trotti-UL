package ca.ulaval.glo4003.trotti.domain.user;

import ca.ulaval.glo4003.trotti.domain.pass.Money;

public class RideBillingResult {
    private final Money additionalFees;
    private final boolean chargedImmediately;
    private final Money monthlyBalanceAfter;

    public RideBillingResult(Money additionalFees, boolean chargedImmediately, Money monthlyBalanceAfter) {
        this.additionalFees = additionalFees;
        this.chargedImmediately = chargedImmediately;
        this.monthlyBalanceAfter = monthlyBalanceAfter;
    }

    public Money getAdditionalFees() {
        return additionalFees;
    }

    public boolean isChargedImmediately() {
        return chargedImmediately;
    }

    public Money getMonthlyBalanceAfter() {
        return monthlyBalanceAfter;
    }
}

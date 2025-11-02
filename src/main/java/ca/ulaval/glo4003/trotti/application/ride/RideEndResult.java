package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.domain.pass.Money;

import java.math.BigDecimal;
import java.time.Duration;

public class RideEndResult {
    private final String rideId;
    private final Duration rideDuration;
    private final Money additionalFees;
    private final boolean chargedImmediately;
    private final Money monthlyBalanceAfter;

    public RideEndResult(String rideId, Duration rideDuration, Money additionalFees,
                         boolean chargedImmediately, Money monthlyBalanceAfter) {
        this.rideId = rideId;
        this.rideDuration = rideDuration;
        this.additionalFees = additionalFees;
        this.chargedImmediately = chargedImmediately;
        this.monthlyBalanceAfter = monthlyBalanceAfter;
    }

    public String getRideId() {
        return rideId;
    }

    public Duration getRideDuration() {
        return rideDuration;
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

    public long getDurationInMinutes() {
        return rideDuration.toMinutes();
    }

    public long getDurationInSeconds() {
        return rideDuration.getSeconds() % 60;
    }

    public BigDecimal getAdditionalFeesAmount() {
        return additionalFees.getAmount();
    }
}

package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto;

import java.math.BigDecimal;

public class EndRideResponse {
    private long durationMinutes;
    private long durationSeconds;
    private BigDecimal additionalFees;
    private boolean chargedImmediately;
    private BigDecimal monthlyBalance;
    private String message;

    public EndRideResponse(long durationMinutes, long durationSeconds,
                           BigDecimal additionalFees, boolean chargedImmediately,
                           BigDecimal monthlyBalance, String message) {
        this.durationMinutes = durationMinutes;
        this.durationSeconds = durationSeconds;
        this.additionalFees = additionalFees;
        this.chargedImmediately = chargedImmediately;
        this.monthlyBalance = monthlyBalance;
        this.message = message;
    }

    public long getDurationMinutes() {
        return durationMinutes;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public BigDecimal getAdditionalFees() {
        return additionalFees;
    }

    public boolean isChargedImmediately() {
        return chargedImmediately;
    }

    public BigDecimal getMonthlyBalance() {
        return monthlyBalance;
    }

    public String getMessage() {
        return message;
    }
}

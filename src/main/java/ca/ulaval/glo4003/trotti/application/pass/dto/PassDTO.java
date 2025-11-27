package ca.ulaval.glo4003.trotti.application.pass.dto;

import java.math.BigDecimal;

public class PassDTO {
    private final String id;
    private final String session;
    private final int extraMinutesPerDay;
    private final boolean monthlyBilling;
    private final BigDecimal totalPrice;
    private final boolean active;

    public PassDTO(String id, String session, int extraMinutesPerDay,
                   boolean monthlyBilling, BigDecimal totalPrice, boolean active) {
        this.id = id;
        this.session = session;
        this.extraMinutesPerDay = extraMinutesPerDay;
        this.monthlyBilling = monthlyBilling;
        this.totalPrice = totalPrice;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public String getSession() {
        return session;
    }

    public int getExtraMinutesPerDay() {
        return extraMinutesPerDay;
    }

    public boolean isMonthlyBilling() {
        return monthlyBilling;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public boolean isActive() {
        return active;
    }
}

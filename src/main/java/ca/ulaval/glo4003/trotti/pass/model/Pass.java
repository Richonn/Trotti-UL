package ca.ulaval.glo4003.trotti.pass.model;

import java.math.BigDecimal;

public class Pass {
    private final Session session;
    private final int extraMinutesPerDay;
    private final boolean monthlyBilling;
    private final BigDecimal totalPrice;

    public Pass(Session session, int extraMinutesPerDay, boolean monthlyBilling, BigDecimal totalPrice) {
        this.session = session;
        this.extraMinutesPerDay = extraMinutesPerDay;
        this.monthlyBilling = monthlyBilling;
        this.totalPrice = totalPrice;
    }

    public Session getSession() {
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
}

package ca.ulaval.glo4003.trotti.interfaces.rest.pass.dto;

import java.math.BigDecimal;

public class PassDto {
    private String id;
    private String session;
    private int extraMinutesPerDay;
    private boolean monthlyBilling;
    private BigDecimal totalPrice;
    private boolean active;

    public PassDto() {
    }

    public PassDto(String id, String session, int extraMinutesPerDay, boolean monthlyBilling,
                   BigDecimal totalPrice, boolean active) {
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

    public void setId(String id) {
        this.id = id;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public int getExtraMinutesPerDay() {
        return extraMinutesPerDay;
    }

    public void setExtraMinutesPerDay(int extraMinutesPerDay) {
        this.extraMinutesPerDay = extraMinutesPerDay;
    }

    public boolean isMonthlyBilling() {
        return monthlyBilling;
    }

    public void setMonthlyBilling(boolean monthlyBilling) {
        this.monthlyBilling = monthlyBilling;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

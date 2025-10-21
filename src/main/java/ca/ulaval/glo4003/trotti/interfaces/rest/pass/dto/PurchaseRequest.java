package ca.ulaval.glo4003.trotti.interfaces.rest.pass.dto;

import ca.ulaval.glo4003.trotti.domain.pass.Session;

public class PurchaseRequest {
    private Session session;
    private int extraMinutesPerDay;
    private boolean monthlyBilling;
    private String cardNumber;
    private String cardHolder;
    private String expiryDate;
    private String cvv;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
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

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}

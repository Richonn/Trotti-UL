package ca.ulaval.glo4003.trotti.pass.model;

public class PaymentInfo {
    private final String cardNumber;
    private final String cardHolder;
    private final String expiryDate;
    private final String cvv;

    public PaymentInfo(String cardNumber, String cardHolder, String expiryDate, String cvv) {
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getCvv() {
        return cvv;
    }
}

package ca.ulaval.glo4003.trotti.domain.pass;

import ca.ulaval.glo4003.trotti.domain.shared.Clock;

import java.util.Objects;

public class PaymentInfo {
    private final CardNumber cardNumber;
    private final String cardHolder;
    private final ExpiryDate expiryDate;
    private final String cvv;

    public PaymentInfo(String cardNumber, String cardHolder, String expiryDate, String cvv) {
        this.cardNumber = new CardNumber(cardNumber);
        this.cardHolder = validateCardHolder(cardHolder);
        this.expiryDate = new ExpiryDate(expiryDate);
        this.cvv = validateCvv(cvv);
    }

    private String validateCardHolder(String cardHolder) {
        if (cardHolder == null || cardHolder.isBlank()) {
            throw new IllegalArgumentException("Card holder cannot be empty");
        }
        return cardHolder;
    }

    private String validateCvv(String cvv) {
        if (cvv == null || cvv.isBlank()) {
            throw new IllegalArgumentException("CVV cannot be empty");
        }
        if (!cvv.matches("\\d{3,4}")) {
            throw new IllegalArgumentException("CVV must be 3 or 4 digits");
        }
        return cvv;
    }

    public CardNumber getCardNumber() {
        return cardNumber;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public ExpiryDate getExpiryDate() {
        return expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public boolean isExpired(Clock clock) {
        return expiryDate.isExpired(clock);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentInfo that = (PaymentInfo) o;
        return Objects.equals(cardNumber, that.cardNumber) &&
                Objects.equals(cardHolder, that.cardHolder) &&
                Objects.equals(expiryDate, that.expiryDate) &&
                Objects.equals(cvv, that.cvv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber, cardHolder, expiryDate, cvv);
    }
}

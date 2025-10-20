package ca.ulaval.glo4003.trotti.domain.pass;

import java.util.Objects;
import java.util.stream.IntStream;

public class CardNumber {
    private final String value;

    public CardNumber(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Card number cannot be empty");
        }

        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid card number (Luhn algorithm failed)");
        }

        this.value = value;
    }

    private boolean isValid(String cardNumber) {
        if (cardNumber.length() < 8 || cardNumber.length() > 16) {
            return false;
        }

        int[] numbers = new int[cardNumber.length()];
        for (int i = 0; i < cardNumber.length(); i++) {
            if (!Character.isDigit(cardNumber.charAt(i))) {
                return false;
            }
            numbers[i] = Character.getNumericValue(cardNumber.charAt(i));
            if ((i + 1) % 2 == 1) {
                numbers[i] = (numbers[i] * 2) % 10 + ((numbers[i] * 2) / 10);
            }
        }

        return IntStream.of(numbers).sum() % 10 == 0;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardNumber that = (CardNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "****" + value.substring(value.length() - 4);
    }
}

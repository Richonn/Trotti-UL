package ca.ulaval.glo4003.trotti.domain.pass;

import java.time.LocalDate;
import java.time.Year;
import java.util.Objects;

public class ExpiryDate {
    private final String value;

    public ExpiryDate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Expiry date cannot be empty");
        }

        if (!value.matches("\\d{2}/\\d{2}")) {
            throw new IllegalArgumentException("Expiry date must be in format MM/YY");
        }

        String[] parts = value.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid month in expiry date");
        }

        if (isExpired(month, year)) {
            throw new IllegalArgumentException("Card is expired");
        }

        this.value = value;
    }

    private boolean isExpired(int month, int year) {
        int currentYear = Year.now().getValue() % 100;
        int currentMonth = LocalDate.now().getMonthValue();
        return year < currentYear || (year == currentYear && month < currentMonth);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpiryDate that = (ExpiryDate) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}

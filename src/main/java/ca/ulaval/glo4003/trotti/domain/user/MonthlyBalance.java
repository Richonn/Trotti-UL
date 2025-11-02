package ca.ulaval.glo4003.trotti.domain.user;

import ca.ulaval.glo4003.trotti.domain.pass.Money;

import java.util.Objects;

public class MonthlyBalance {
    private final Money amount;

    public MonthlyBalance() {
        this.amount = Money.zero();
    }

    public MonthlyBalance(Money amount) {
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
    }

    public MonthlyBalance add(Money additionalAmount) {
        return new MonthlyBalance(amount.add(additionalAmount));
    }

    public Money getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonthlyBalance that = (MonthlyBalance) o;
        return Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    @Override
    public String toString() {
        return "MonthlyBalance{" +
                "amount=" + amount +
                '}';
    }
}

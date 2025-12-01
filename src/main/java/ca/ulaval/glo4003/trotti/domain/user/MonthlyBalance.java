package ca.ulaval.glo4003.trotti.domain.user;

import ca.ulaval.glo4003.trotti.domain.pass.Money;

import java.time.LocalDate;
import java.util.Objects;

public class MonthlyBalance {
    private final Money amount;
    private final LocalDate balanceStartDate;

    public MonthlyBalance() {
        this.amount = Money.zero();
        this.balanceStartDate = null;
    }

    public MonthlyBalance(Money amount) {
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.balanceStartDate = null;
    }

    public MonthlyBalance(Money amount, LocalDate balanceStartDate) {
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.balanceStartDate = balanceStartDate;
    }

    public MonthlyBalance add(Money additionalAmount) {
        return new MonthlyBalance(amount.add(additionalAmount));
    }

    public MonthlyBalance add(Money additionalAmount, LocalDate currentDate) {
        LocalDate startDate = this.balanceStartDate;
        if (startDate == null && additionalAmount.getAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            startDate = currentDate;
        }
        return new MonthlyBalance(amount.add(additionalAmount), startDate);
    }

    public Money getAmount() {
        return amount;
    }

    public LocalDate getBalanceStartDate() {
        return balanceStartDate;
    }

    public boolean isOverdue(LocalDate currentDate, int gracePeriodDays) {
        if (balanceStartDate == null || amount.getAmount().compareTo(java.math.BigDecimal.ZERO) == 0) {
            return false;
        }

        LocalDate startOfMonth = balanceStartDate.withDayOfMonth(1);
        LocalDate dueDate = startOfMonth.plusDays(gracePeriodDays);

        return currentDate.isAfter(dueDate) && amount.getAmount().compareTo(java.math.BigDecimal.ZERO) > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonthlyBalance that = (MonthlyBalance) o;
        return Objects.equals(amount, that.amount) &&
               Objects.equals(balanceStartDate, that.balanceStartDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, balanceStartDate);
    }

    @Override
    public String toString() {
        return "MonthlyBalance{" +
                "amount=" + amount +
                ", balanceStartDate=" + balanceStartDate +
                '}';
    }
}

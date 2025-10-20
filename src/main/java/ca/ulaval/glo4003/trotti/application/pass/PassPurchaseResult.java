package ca.ulaval.glo4003.trotti.application.pass;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PassPurchaseResult {
    private final String passId;
    private final String sessionCode;
    private final BigDecimal totalPrice;
    private final boolean monthlyBilling;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public PassPurchaseResult(String passId, String sessionCode, BigDecimal totalPrice,
                               boolean monthlyBilling, LocalDate startDate, LocalDate endDate) {
        this.passId = passId;
        this.sessionCode = sessionCode;
        this.totalPrice = totalPrice;
        this.monthlyBilling = monthlyBilling;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getPassId() {
        return passId;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public boolean isMonthlyBilling() {
        return monthlyBilling;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}

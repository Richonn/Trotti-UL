package ca.ulaval.glo4003.trotti.interfaces.rest.user.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceDto {
    private BigDecimal totalPrice;
    private boolean monthlyBilling;

    private LocalDate startDate;
    private LocalDate endDate;

    public InvoiceDto(BigDecimal totalPrice, boolean monthlyBilling, LocalDate startDate, LocalDate endDate) {
        this.totalPrice = totalPrice;
        this.monthlyBilling = monthlyBilling;
        this.startDate = startDate;
        this.endDate = endDate;
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

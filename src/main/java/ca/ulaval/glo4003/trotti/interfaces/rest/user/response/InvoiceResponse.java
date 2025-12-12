package ca.ulaval.glo4003.trotti.interfaces.rest.user.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceResponse {
    private BigDecimal totalPrice;
    private boolean monthlyBilling;

    private LocalDate startDate;
    private LocalDate endDate;

    public InvoiceResponse(BigDecimal totalPrice, boolean monthlyBilling, LocalDate startDate, LocalDate endDate) {
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

}

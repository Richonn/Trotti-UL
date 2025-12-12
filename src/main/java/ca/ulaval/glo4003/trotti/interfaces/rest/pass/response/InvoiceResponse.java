package ca.ulaval.glo4003.trotti.interfaces.rest.pass.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceResponse {
    private String session;
    private BigDecimal totalPrice;
    private boolean monthlyBilling;

    private LocalDate startDate;
    private LocalDate endDate;

    public InvoiceResponse(String session, BigDecimal totalPrice, boolean monthlyBilling, LocalDate startDate, LocalDate endDate) {
        this.session = session;
        this.totalPrice = totalPrice;
        this.monthlyBilling = monthlyBilling;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getSession() {
        return session;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public boolean isMonthlyBilling() {
        return monthlyBilling;
    }
}

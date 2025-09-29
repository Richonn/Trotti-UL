package ca.ulaval.glo4003.trotti.pass.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceDto {
    private String session;
    private BigDecimal totalPrice;
    private boolean monthlyBilling;

    private LocalDate startDate;
    private LocalDate endDate;

    public InvoiceDto(String session, BigDecimal totalPrice, boolean monthlyBilling, LocalDate startDate, LocalDate endDate) {
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

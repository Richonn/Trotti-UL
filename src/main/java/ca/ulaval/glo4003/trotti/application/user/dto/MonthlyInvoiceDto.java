package ca.ulaval.glo4003.trotti.application.user.dto;

import java.time.LocalDate;

import ca.ulaval.glo4003.trotti.domain.pass.Money;

public class MonthlyInvoiceDto {

    private final Money amount;
    private final LocalDate start;
    private final LocalDate end;

    public MonthlyInvoiceDto(final Money amount, final LocalDate start, final LocalDate end) {
        this.amount = amount;
        this.start = start;
        this.end = end;
    }

    public Money getAmount() {
        return amount;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

}

package ca.ulaval.glo4003.trotti.interfaces.rest.user.mapper;

import ca.ulaval.glo4003.trotti.application.user.dto.MonthlyInvoiceDto;
import ca.ulaval.glo4003.trotti.interfaces.rest.user.dto.InvoiceDto;

public class InvoiceResponseMapper {

    public static InvoiceDto toDto(MonthlyInvoiceDto monthlyInvoiceDto) {
        return new InvoiceDto(
                monthlyInvoiceDto.getAmount().getAmount(),
                true,
                monthlyInvoiceDto.getStart(),
                monthlyInvoiceDto.getEnd()
        );
    }
}

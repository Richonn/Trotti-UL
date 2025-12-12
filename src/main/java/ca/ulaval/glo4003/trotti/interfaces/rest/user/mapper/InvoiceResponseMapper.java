package ca.ulaval.glo4003.trotti.interfaces.rest.user.mapper;

import ca.ulaval.glo4003.trotti.application.user.dto.MonthlyInvoiceDto;
import ca.ulaval.glo4003.trotti.interfaces.rest.user.response.InvoiceResponse;

public class InvoiceResponseMapper {

    public static InvoiceResponse toDto(MonthlyInvoiceDto monthlyInvoiceDto) {
        return new InvoiceResponse(
                monthlyInvoiceDto.getAmount().getAmount(),
                true,
                monthlyInvoiceDto.getStart(),
                monthlyInvoiceDto.getEnd()
        );
    }
}

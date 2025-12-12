package ca.ulaval.glo4003.trotti.interfaces.rest.pass.mapper;

import ca.ulaval.glo4003.trotti.application.pass.dto.PassPurchaseResult;
import ca.ulaval.glo4003.trotti.application.pass.dto.PassDTO;
import ca.ulaval.glo4003.trotti.domain.pass.Session;
import ca.ulaval.glo4003.trotti.interfaces.rest.pass.response.InvoiceResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.pass.response.PassResponse;

import java.util.List;

public class PassResponseMapper {

    public static InvoiceResponse toInvoiceDto(PassPurchaseResult result) {
        Session sessionEnum = Session.getInstance(result.getSessionCode());
        return new InvoiceResponse(
                sessionEnum.getId(),
                result.getTotalPrice(),
                result.isMonthlyBilling(),
                result.getStartDate(),
                result.getEndDate()
        );
    }

    public static PassResponse toDto(PassDTO passDTO) {
        return new PassResponse(
                passDTO.getId(),
                passDTO.getSession(),
                passDTO.getExtraMinutesPerDay(),
                passDTO.isMonthlyBilling(),
                passDTO.getTotalPrice(),
                passDTO.isActive()
        );
    }

    public static List<PassResponse> toDtoList(List<PassDTO> passDTOs) {
        return passDTOs.stream()
                .map(PassResponseMapper::toDto)
                .toList();
    }
}

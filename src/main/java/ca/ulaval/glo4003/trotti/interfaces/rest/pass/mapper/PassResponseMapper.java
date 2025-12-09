package ca.ulaval.glo4003.trotti.interfaces.rest.pass.mapper;

import ca.ulaval.glo4003.trotti.application.pass.dto.PassPurchaseResult;
import ca.ulaval.glo4003.trotti.application.pass.dto.PassDTO;
import ca.ulaval.glo4003.trotti.domain.pass.Session;
import ca.ulaval.glo4003.trotti.interfaces.rest.pass.dto.InvoiceDto;
import ca.ulaval.glo4003.trotti.interfaces.rest.pass.dto.PassDto;

import java.util.List;

public class PassResponseMapper {

    public static InvoiceDto toInvoiceDto(PassPurchaseResult result) {
        Session sessionEnum = Session.getInstance(result.getSessionCode());
        return new InvoiceDto(
                sessionEnum.name(),
                result.getTotalPrice(),
                result.isMonthlyBilling(),
                result.getStartDate(),
                result.getEndDate()
        );
    }

    public static PassDto toDto(PassDTO passDTO) {
        return new PassDto(
                passDTO.getId(),
                passDTO.getSession(),
                passDTO.getExtraMinutesPerDay(),
                passDTO.isMonthlyBilling(),
                passDTO.getTotalPrice(),
                passDTO.isActive()
        );
    }

    public static List<PassDto> toDtoList(List<PassDTO> passDTOs) {
        return passDTOs.stream()
                .map(PassResponseMapper::toDto)
                .toList();
    }
}

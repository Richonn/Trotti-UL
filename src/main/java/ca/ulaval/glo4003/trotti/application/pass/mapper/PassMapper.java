package ca.ulaval.glo4003.trotti.application.pass.mapper;

import ca.ulaval.glo4003.trotti.application.pass.dto.PassDTO;
import ca.ulaval.glo4003.trotti.domain.pass.Pass;

public class PassMapper {

    public static PassDTO toDTO(Pass pass) {
        return new PassDTO(
            pass.getId(),
            pass.getSession().name(),
            pass.getExtraMinutesPerDay(),
            pass.isMonthlyBilling(),
            pass.getTotalPrice(),
            pass.isActive()
        );
    }
}

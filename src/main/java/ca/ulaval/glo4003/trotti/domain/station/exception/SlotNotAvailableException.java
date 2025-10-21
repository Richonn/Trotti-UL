package ca.ulaval.glo4003.trotti.domain.station.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class SlotNotAvailableException extends DomainException {
    public SlotNotAvailableException(String message) {
        super(message);
    }
}

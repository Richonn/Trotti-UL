package ca.ulaval.glo4003.trotti.domain.scooter.exception;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;

public class LowBatteryException extends DomainException {
    public LowBatteryException(double batteryLevel) {
        super(String.format("Scooter battery too low: %.1f%%", batteryLevel));
    }
}

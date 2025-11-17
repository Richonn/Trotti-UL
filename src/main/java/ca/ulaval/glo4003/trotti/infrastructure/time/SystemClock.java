package ca.ulaval.glo4003.trotti.infrastructure.time;

import ca.ulaval.glo4003.trotti.domain.shared.Clock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

public class SystemClock implements Clock {
    @Override
    public LocalDate today() {
        return LocalDate.now();
    }

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    @Override
    public Year currentYear() {
        return Year.now();
    }
}

package ca.ulaval.glo4003.trotti.infrastructure.time;

import ca.ulaval.glo4003.trotti.domain.shared.Clock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

public class FixedClock implements Clock {
    private final LocalDate fixedDate;
    private final LocalDateTime fixedDateTime;

    public FixedClock(LocalDate fixedDate) {
        this.fixedDate = fixedDate;
        this.fixedDateTime = fixedDate.atStartOfDay();
    }

    public FixedClock(LocalDateTime fixedDateTime) {
        this.fixedDateTime = fixedDateTime;
        this.fixedDate = fixedDateTime.toLocalDate();
    }

    @Override
    public LocalDate today() {
        return fixedDate;
    }

    @Override
    public LocalDateTime now() {
        return fixedDateTime;
    }

    @Override
    public Year currentYear() {
        return Year.of(fixedDate.getYear());
    }
}

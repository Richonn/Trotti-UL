package ca.ulaval.glo4003.trotti.domain.shared;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

public interface Clock {
    LocalDate today();
    LocalDateTime now();
    Year currentYear();
}

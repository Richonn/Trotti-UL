package ca.ulaval.glo4003.trotti.domain.pass;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

public enum Session {
    A2025("Automne 2025", LocalDate.of(2025, Month.SEPTEMBER, 1), LocalDate.of(2025, Month.DECEMBER, 31)),
    H2026("Hiver 2026", LocalDate.of(2026, Month.JANUARY, 1), LocalDate.of(2026, Month.APRIL, 30)),
    E2026("Été 2026", LocalDate.of(2026, Month.MAY, 1), LocalDate.of(2026, Month.AUGUST, 31));

    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;

    Session(String description, LocalDate startDate, LocalDate endDate) {
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getDurationInMonths() {
        return (int) ChronoUnit.MONTHS.between(startDate, endDate.plusDays(1));
    }

    public boolean contains(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public static Session getInstance(String value) {
        for (Session session : Session.values()) {
            if (session.name().equalsIgnoreCase(value)) {
                return session;
            }
        }
        throw new IllegalArgumentException("Session inconnue: " + value);
    }
}

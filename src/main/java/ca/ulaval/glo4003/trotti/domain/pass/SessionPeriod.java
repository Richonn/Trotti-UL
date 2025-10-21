package ca.ulaval.glo4003.trotti.domain.pass;

import java.time.LocalDate;
import java.time.Month;
import java.util.Objects;

public class SessionPeriod {
    private final String code;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int durationInMonths;

    private SessionPeriod(String code, LocalDate startDate, int durationInMonths) {
        this.code = code;
        this.startDate = startDate;
        this.durationInMonths = durationInMonths;
        this.endDate = startDate.plusMonths(durationInMonths);
    }

    public static SessionPeriod fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Session code cannot be empty");
        }

        String prefix = code.substring(0, 1);
        int year;

        try {
            if (code.startsWith("AN")) {
                prefix = "AN";
                year = Integer.parseInt(code.substring(2));
            } else {
                year = Integer.parseInt(code.replaceAll("\\D+", ""));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid session code format: " + code);
        }

        return switch (prefix) {
            case "A" -> new SessionPeriod(code, LocalDate.of(year, Month.SEPTEMBER, 1), 4);
            case "H" -> new SessionPeriod(code, LocalDate.of(year, Month.JANUARY, 1), 4);
            case "E" -> new SessionPeriod(code, LocalDate.of(year, Month.MAY, 1), 4);
            case "AN" -> new SessionPeriod(code, LocalDate.of(year, Month.SEPTEMBER, 1), 12);
            default -> throw new IllegalArgumentException("Unknown session prefix: " + prefix);
        };
    }

    public String getCode() {
        return code;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getDurationInMonths() {
        return durationInMonths;
    }

    public boolean contains(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionPeriod that = (SessionPeriod) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code;
    }
}

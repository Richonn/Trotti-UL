package ca.ulaval.glo4003.trotti.domain.pass;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Session {
    private final String id;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;

    private Session(String id, String description, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Session generate(int year, String season) {
        LocalDate start = calculateStartDate(year, season);
        LocalDate end = calculateEndDate(start, season);
        String id = formatId(season, year);
        String description = formatDescription(season, year);
        return new Session(id, description, start, end);
    }

    public static Session fromId(String id) {
        if (id == null || id.length() < 5) {
            throw new IllegalArgumentException("Invalid session ID: " + id);
        }

        String seasonCode = id.substring(0, 1);
        int year;
        try {
            year = Integer.parseInt(id.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid session ID: " + id);
        }

        String season = switch (seasonCode) {
            case "A" -> "Automne";
            case "H" -> "Hiver";
            case "E" -> "Été";
            default -> throw new IllegalArgumentException("Invalid season code: " + seasonCode);
        };

        return generate(year, season);
    }

    private static String formatId(String season, int year) {
        String code = switch (season) {
            case "Automne" -> "A";
            case "Hiver" -> "H";
            case "Été", "Ete" -> "E";
            default -> throw new IllegalArgumentException("Invalid season: " + season);
        };
        return code + year;
    }

    private static String formatDescription(String season, int year) {
        return season + " " + year;
    }

    private static LocalDate calculateStartDate(int year, String season) {
        return switch(season) {
            case "Automne" -> LocalDate.of(year, Month.SEPTEMBER, 1);
            case "Hiver" -> LocalDate.of(year, Month.JANUARY, 1);
            case "Été", "Ete" -> LocalDate.of(year, Month.MAY, 1);
            default -> throw new IllegalArgumentException("Invalid season: " + season);
        };
    }

    private static LocalDate calculateEndDate(LocalDate start, String season) {
        return switch(season) {
            case "Automne" -> LocalDate.of(start.getYear(), Month.DECEMBER, 31);
            case "Hiver" -> LocalDate.of(start.getYear(), Month.APRIL, 30);
            case "Été", "Ete" -> LocalDate.of(start.getYear(), Month.AUGUST, 31);
            default -> throw new IllegalArgumentException("Invalid season: " + season);
        };
    }

    public String getId() {
        return id;
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

    public boolean isActive(LocalDate currentDate) {
        return !currentDate.isAfter(endDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(id, session.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return description;
    }

    public static Session getInstance(String value) {
        return fromId(value);
    }
}

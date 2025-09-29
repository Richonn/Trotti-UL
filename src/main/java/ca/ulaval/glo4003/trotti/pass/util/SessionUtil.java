package ca.ulaval.glo4003.trotti.pass.util;

import ca.ulaval.glo4003.trotti.pass.model.Session;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

public class SessionUtil {

    private static final Map<String, Integer> SESSION_MONTHS = new HashMap<>();

    static {
        SESSION_MONTHS.put("A", 4);
        SESSION_MONTHS.put("H", 4);
        SESSION_MONTHS.put("E", 4);
        SESSION_MONTHS.put("AN", 12);
    }

    public static int getSessionDurationInMonths(Session session) {
        String code = session.name();
        if (code.startsWith("AN")) {
            return SESSION_MONTHS.get("AN");
        }
        return SESSION_MONTHS.getOrDefault(code.substring(0, 1), 4);
    }

    public static LocalDate getSessionStartDate(Session session) {
        String code = session.name();
        String prefix = code.substring(0, 1);
        int year = Integer.parseInt(code.replaceAll("\\D+", ""));

        return switch (prefix) {
            case "A" -> LocalDate.of(year, Month.SEPTEMBER, 1);
            case "H" -> LocalDate.of(year, Month.JANUARY, 1);
            case "E" -> LocalDate.of(year, Month.MAY, 1);
            case "AN" -> LocalDate.of(year, Month.SEPTEMBER, 1);
            default -> throw new IllegalArgumentException("Unknown session: " + prefix);
        };
    }

    public static LocalDate getSessionEndDate(Session session) {
        LocalDate start = getSessionStartDate(session);
        int months = getSessionDurationInMonths(session);
        return start.plusMonths(months);
    }
}

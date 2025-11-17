package ca.ulaval.glo4003.trotti.domain.ride;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DailyUsageTracker {
    private final Map<String, Map<LocalDate, Integer>> usageByUserAndDate = new HashMap<>();

    public void recordUsage(String userId, LocalDate date, int minutesUsed) {
        usageByUserAndDate
            .computeIfAbsent(userId, k -> new HashMap<>())
            .merge(date, minutesUsed, Integer::sum);
    }

    public int getUsageForDay(String userId, LocalDate date) {
        return usageByUserAndDate
            .getOrDefault(userId, new HashMap<>())
            .getOrDefault(date, 0);
    }

    public void reset(String userId, LocalDate date) {
        Map<LocalDate, Integer> userUsage = usageByUserAndDate.get(userId);
        if (userUsage != null) {
            userUsage.remove(date);
        }
    }
}

package ca.ulaval.glo4003.trotti.infrastructure.persistence.memory;

import ca.ulaval.glo4003.trotti.domain.ride.DailyUsageRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDailyUsageRepository implements DailyUsageRepository {
    private final Map<String, Map<LocalDate, Integer>> usageByUserAndDate = new ConcurrentHashMap<>();

    @Override
    public void recordUsage(String userId, LocalDate date, int minutesUsed) {
        usageByUserAndDate
            .computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
            .merge(date, minutesUsed, Integer::sum);
    }

    @Override
    public int getUsageForDay(String userId, LocalDate date) {
        return usageByUserAndDate
            .getOrDefault(userId, Map.of())
            .getOrDefault(date, 0);
    }

    @Override
    public void reset(String userId, LocalDate date) {
        Map<LocalDate, Integer> userUsage = usageByUserAndDate.get(userId);
        if (userUsage != null) {
            userUsage.remove(date);
        }
    }
}

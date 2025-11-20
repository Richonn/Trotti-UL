package ca.ulaval.glo4003.trotti.domain.ride;

import java.time.LocalDate;

public interface DailyUsageRepository {
    void recordUsage(String userId, LocalDate date, int minutesUsed);

    int getUsageForDay(String userId, LocalDate date);

    void reset(String userId, LocalDate date);
}

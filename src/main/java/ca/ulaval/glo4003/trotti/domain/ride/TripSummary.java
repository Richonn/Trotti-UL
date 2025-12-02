package ca.ulaval.glo4003.trotti.domain.ride;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TripSummary {
    private final int totalTrips;
    private final long totalDurationMinutes;
    private final long averageDurationMinutes;
    private final String favoriteStationId;

    private TripSummary(int totalTrips, long totalDurationMinutes, long averageDurationMinutes, String favoriteStationId) {
        this.totalTrips = totalTrips;
        this.totalDurationMinutes = totalDurationMinutes;
        this.averageDurationMinutes = averageDurationMinutes;
        this.favoriteStationId = favoriteStationId;
    }

    public static TripSummary from(List<Ride> completedRides) {
        if (completedRides.isEmpty()) {
            return new TripSummary(0, 0, 0, null);
        }

        int totalTrips = completedRides.size();
        long totalDurationMinutes = completedRides.stream()
                .mapToLong(Ride::getDurationMinutes)
                .sum();
        long averageDurationMinutes = totalDurationMinutes / totalTrips;
        String favoriteStationId = findFavoriteStation(completedRides);

        return new TripSummary(totalTrips, totalDurationMinutes, averageDurationMinutes, favoriteStationId);
    }

    private static String findFavoriteStation(List<Ride> rides) {
        Map<String, Integer> stationFrequency = new HashMap<>();

        for (Ride ride : rides) {
            stationFrequency.merge(ride.getStartStationId(), 1, Integer::sum);
            stationFrequency.merge(ride.getEndStationId(), 1, Integer::sum);
        }

        return stationFrequency.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public int getTotalTrips() {
        return totalTrips;
    }

    public long getTotalDurationMinutes() {
        return totalDurationMinutes;
    }

    public long getAverageDurationMinutes() {
        return averageDurationMinutes;
    }

    public String getFavoriteStationId() {
        return favoriteStationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripSummary that = (TripSummary) o;
        return totalTrips == that.totalTrips &&
                totalDurationMinutes == that.totalDurationMinutes &&
                averageDurationMinutes == that.averageDurationMinutes &&
                Objects.equals(favoriteStationId, that.favoriteStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalTrips, totalDurationMinutes, averageDurationMinutes, favoriteStationId);
    }
}

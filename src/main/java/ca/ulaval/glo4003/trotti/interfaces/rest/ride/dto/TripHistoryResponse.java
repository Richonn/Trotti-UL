package ca.ulaval.glo4003.trotti.interfaces.rest.ride.dto;

import java.util.List;

public class TripHistoryResponse {
    private List<TripItem> trips;
    private TripSummaryDto summary;

    public TripHistoryResponse(List<TripItem> trips, TripSummaryDto summary) {
        this.trips = trips;
        this.summary = summary;
    }

    public List<TripItem> getTrips() {
        return trips;
    }

    public void setTrips(List<TripItem> trips) {
        this.trips = trips;
    }

    public TripSummaryDto getSummary() {
        return summary;
    }

    public void setSummary(TripSummaryDto summary) {
        this.summary = summary;
    }

    public static class TripItem {
        private String rideId;
        private String startStation;
        private String endStation;
        private String startTime;
        private String endTime;
        private int durationMinutes;
        private int durationSeconds;

        public TripItem(String rideId, String startStation, String endStation,
                        String startTime, String endTime, int durationMinutes, int durationSeconds) {
            this.rideId = rideId;
            this.startStation = startStation;
            this.endStation = endStation;
            this.startTime = startTime;
            this.endTime = endTime;
            this.durationMinutes = durationMinutes;
            this.durationSeconds = durationSeconds;
        }

        public String getRideId() {
            return rideId;
        }

        public void setRideId(String rideId) {
            this.rideId = rideId;
        }

        public String getStartStation() {
            return startStation;
        }

        public void setStartStation(String startStation) {
            this.startStation = startStation;
        }

        public String getEndStation() {
            return endStation;
        }

        public void setEndStation(String endStation) {
            this.endStation = endStation;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public int getDurationMinutes() {
            return durationMinutes;
        }

        public void setDurationMinutes(int durationMinutes) {
            this.durationMinutes = durationMinutes;
        }

        public int getDurationSeconds() {
            return durationSeconds;
        }

        public void setDurationSeconds(int durationSeconds) {
            this.durationSeconds = durationSeconds;
        }
    }

    public static class TripSummaryDto {
        private int totalTrips;
        private long totalDurationMinutes;
        private long averageDurationMinutes;
        private String favoriteStation;

        public TripSummaryDto(int totalTrips, long totalDurationMinutes, long averageDurationMinutes, String favoriteStation) {
            this.totalTrips = totalTrips;
            this.totalDurationMinutes = totalDurationMinutes;
            this.averageDurationMinutes = averageDurationMinutes;
            this.favoriteStation = favoriteStation;
        }

        public int getTotalTrips() {
            return totalTrips;
        }

        public void setTotalTrips(int totalTrips) {
            this.totalTrips = totalTrips;
        }

        public long getTotalDurationMinutes() {
            return totalDurationMinutes;
        }

        public void setTotalDurationMinutes(long totalDurationMinutes) {
            this.totalDurationMinutes = totalDurationMinutes;
        }

        public long getAverageDurationMinutes() {
            return averageDurationMinutes;
        }

        public void setAverageDurationMinutes(long averageDurationMinutes) {
            this.averageDurationMinutes = averageDurationMinutes;
        }

        public String getFavoriteStation() {
            return favoriteStation;
        }

        public void setFavoriteStation(String favoriteStation) {
            this.favoriteStation = favoriteStation;
        }
    }
}

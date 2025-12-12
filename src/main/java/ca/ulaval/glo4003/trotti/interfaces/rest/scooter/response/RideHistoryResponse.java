package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.response;

import java.util.List;

public class RideHistoryResponse {
    private List<RideHistoryItem> rides;

    public RideHistoryResponse(List<RideHistoryItem> rides) {
        this.rides = rides;
    }

    public List<RideHistoryItem> getRides() {
        return rides;
    }

    public void setRides(List<RideHistoryItem> rides) {
        this.rides = rides;
    }

    public static class RideHistoryItem {
        private String rideId;
        private String startStation;
        private String endStation;
        private String startTime;
        private String endTime;
        private int durationMinutes;
        private int durationSeconds;

        public RideHistoryItem(String rideId, String startStation, String endStation,
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
}

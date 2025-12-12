package ca.ulaval.glo4003.trotti.application.ride.dto;

import ca.ulaval.glo4003.trotti.domain.ride.RideStatus;

import java.time.LocalDateTime;

public class RideDTO {
    private final String id;
    private final String userId;
    private final String scooterId;
    private final String startStationId;
    private final String endStationId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final RideStatus status;

    public RideDTO(String id, String userId, String scooterId, String startStationId,
                   String endStationId, LocalDateTime startTime, LocalDateTime endTime,
                   RideStatus status) {
        this.id = id;
        this.userId = userId;
        this.scooterId = scooterId;
        this.startStationId = startStationId;
        this.endStationId = endStationId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getScooterId() {
        return scooterId;
    }

    public String getStartStationId() {
        return startStationId;
    }

    public String getEndStationId() {
        return endStationId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public RideStatus getStatus() {
        return status;
    }
}

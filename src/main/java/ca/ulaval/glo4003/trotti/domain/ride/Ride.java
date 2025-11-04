package ca.ulaval.glo4003.trotti.domain.ride;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Ride {
    private final String id;
    private final String userId;
    private final String scooterId;
    private final String startStationId;
    private final LocalDateTime startTime;
    private LocalDateTime endTime;
    private String endStationId;
    private RideStatus status;

    private Ride(String id, String userId, String scooterId, String startStationId, LocalDateTime startTime) {
        this.id = Objects.requireNonNull(id, "Ride ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.scooterId = Objects.requireNonNull(scooterId, "Scooter ID cannot be null");
        this.startStationId = Objects.requireNonNull(startStationId, "Start station ID cannot be null");
        this.startTime = Objects.requireNonNull(startTime, "Start time cannot be null");
        this.endTime = null;
        this.endStationId = null;
        this.status = RideStatus.ACTIVE;
    }

    public static Ride start(String userId, String scooterId, String startStationId, LocalDateTime startTime) {
        return new Ride(UUID.randomUUID().toString(), userId, scooterId, startStationId, startTime);
    }

    public void end(LocalDateTime endTime, String endStationId) {
        if (status == RideStatus.COMPLETED) {
            throw new IllegalStateException("Ride is already completed");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }
        this.endTime = endTime;
        this.endStationId = Objects.requireNonNull(endStationId, "End station ID cannot be null");
        this.status = RideStatus.COMPLETED;
    }

    public Duration getDuration() {
        LocalDateTime end = endTime != null ? endTime : LocalDateTime.now();
        return Duration.between(startTime, end);
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public RideStatus getStatus() {
        return status;
    }

    public String getStartStationId() {
        return startStationId;
    }

    public String getEndStationId() {
        return endStationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ride ride = (Ride) o;
        return Objects.equals(id, ride.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package ca.ulaval.glo4003.trotti.domain.ride;

import ca.ulaval.glo4003.trotti.domain.shared.Clock;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Ride {
    private final String id;
    private final String userId;
    private final String scooterId;
    private final LocalDateTime startTime;
    private LocalDateTime endTime;
    private RideStatus status;

    private Ride(String id, String userId, String scooterId, LocalDateTime startTime) {
        this.id = Objects.requireNonNull(id, "Ride ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.scooterId = Objects.requireNonNull(scooterId, "Scooter ID cannot be null");
        this.startTime = Objects.requireNonNull(startTime, "Start time cannot be null");
        this.endTime = null;
        this.status = RideStatus.ACTIVE;
    }

    public static Ride start(String userId, String scooterId, LocalDateTime startTime) {
        return new Ride(UUID.randomUUID().toString(), userId, scooterId, startTime);
    }

    public void end(LocalDateTime endTime) {
        if (status == RideStatus.COMPLETED) {
            throw new IllegalStateException("Ride is already completed");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }
        this.endTime = endTime;
        this.status = RideStatus.COMPLETED;
    }

    public Duration getDuration(Clock clock) {
        LocalDateTime end = endTime != null ? endTime : clock.now();
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

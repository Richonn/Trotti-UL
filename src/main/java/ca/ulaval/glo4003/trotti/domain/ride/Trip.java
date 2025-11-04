package ca.ulaval.glo4003.trotti.domain.ride;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Trip {
    private final String id;
    private final String userId;
    private final String rideId;
    private final String startStationId;
    private final String endStationId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Duration duration;
    private final BigDecimal additionalFees;

    private Trip(String id, String userId, String rideId, String startStationId, String endStationId,
                 LocalDateTime startTime, LocalDateTime endTime, Duration duration, BigDecimal additionalFees) {
        this.id = Objects.requireNonNull(id, "Trip ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.rideId = Objects.requireNonNull(rideId, "Ride ID cannot be null");
        this.startStationId = Objects.requireNonNull(startStationId, "Start station ID cannot be null");
        this.endStationId = Objects.requireNonNull(endStationId, "End station ID cannot be null");
        this.startTime = Objects.requireNonNull(startTime, "Start time cannot be null");
        this.endTime = Objects.requireNonNull(endTime, "End time cannot be null");
        this.duration = Objects.requireNonNull(duration, "Duration cannot be null");
        this.additionalFees = Objects.requireNonNull(additionalFees, "Additional fees cannot be null");
    }

    public static Trip create(String userId, String rideId, String startStationId, String endStationId,
                              LocalDateTime startTime, LocalDateTime endTime, BigDecimal additionalFees) {
        Duration duration = Duration.between(startTime, endTime);
        return new Trip(
            UUID.randomUUID().toString(),
            userId,
            rideId,
            startStationId,
            endStationId,
            startTime,
            endTime,
            duration,
            additionalFees
        );
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getRideId() {
        return rideId;
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

    public Duration getDuration() {
        return duration;
    }

    public BigDecimal getAdditionalFees() {
        return additionalFees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return Objects.equals(id, trip.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

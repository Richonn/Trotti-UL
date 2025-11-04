package ca.ulaval.glo4003.trotti.application.ride;

import java.math.BigDecimal;
import java.time.Duration;

public class RideReturnResult {
    private final String tripId;
    private final String startStationId;
    private final String endStationId;
    private final Duration duration;
    private final BigDecimal additionalFees;
    private final boolean quotaExceeded;

    public RideReturnResult(String tripId, String startStationId, String endStationId,
                           Duration duration, BigDecimal additionalFees, boolean quotaExceeded) {
        this.tripId = tripId;
        this.startStationId = startStationId;
        this.endStationId = endStationId;
        this.duration = duration;
        this.additionalFees = additionalFees;
        this.quotaExceeded = quotaExceeded;
    }

    public String getTripId() {
        return tripId;
    }

    public String getStartStationId() {
        return startStationId;
    }

    public String getEndStationId() {
        return endStationId;
    }

    public Duration getDuration() {
        return duration;
    }

    public BigDecimal getAdditionalFees() {
        return additionalFees;
    }

    public boolean isQuotaExceeded() {
        return quotaExceeded;
    }
}

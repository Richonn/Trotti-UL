package ca.ulaval.glo4003.trotti.interfaces.rest.ride.dto;

import java.math.BigDecimal;

public class ReturnRideResponse {
    private String tripId;
    private String startStation;
    private String endStation;
    private long durationMinutes;
    private BigDecimal additionalFees;
    private boolean quotaExceeded;
    private String message;

    public ReturnRideResponse(String tripId, String startStation, String endStation,
                             long durationMinutes, BigDecimal additionalFees, boolean quotaExceeded) {
        this.tripId = tripId;
        this.startStation = startStation;
        this.endStation = endStation;
        this.durationMinutes = durationMinutes;
        this.additionalFees = additionalFees;
        this.quotaExceeded = quotaExceeded;

        if (quotaExceeded) {
            this.message = "Ride returned successfully. Daily quota exceeded, additional fee of $" +
                          additionalFees + " has been charged.";
        } else {
            this.message = "Ride returned successfully. No additional fees.";
        }
    }

    public String getTripId() {
        return tripId;
    }

    public String getStartStation() {
        return startStation;
    }

    public String getEndStation() {
        return endStation;
    }

    public long getDurationMinutes() {
        return durationMinutes;
    }

    public BigDecimal getAdditionalFees() {
        return additionalFees;
    }

    public boolean isQuotaExceeded() {
        return quotaExceeded;
    }

    public String getMessage() {
        return message;
    }
}

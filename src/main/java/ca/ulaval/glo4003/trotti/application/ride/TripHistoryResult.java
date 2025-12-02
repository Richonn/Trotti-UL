package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.domain.ride.Ride;
import ca.ulaval.glo4003.trotti.domain.ride.TripSummary;

import java.util.List;

public class TripHistoryResult {
    private final List<Ride> rides;
    private final TripSummary summary;

    public TripHistoryResult(List<Ride> rides, TripSummary summary) {
        this.rides = rides;
        this.summary = summary;
    }

    public List<Ride> getRides() {
        return rides;
    }

    public TripSummary getSummary() {
        return summary;
    }
}

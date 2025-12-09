package ca.ulaval.glo4003.trotti.interfaces.rest.ride.mapper;

import ca.ulaval.glo4003.trotti.application.ride.dto.RideEndResult;
import ca.ulaval.glo4003.trotti.application.ride.dto.RideStartResult;
import ca.ulaval.glo4003.trotti.application.ride.TripHistoryResult;
import ca.ulaval.glo4003.trotti.domain.ride.Ride;
import ca.ulaval.glo4003.trotti.domain.ride.RideCode;
import ca.ulaval.glo4003.trotti.interfaces.rest.ride.dto.TripHistoryResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto.EndRideResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto.RideHistoryResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto.StartRideResponse;

import java.util.List;
import java.util.stream.Collectors;

public class RideResponseMapper {

    public static ca.ulaval.glo4003.trotti.interfaces.rest.scooter.model.RideCode toRideCodeModel(RideCode rideCode) {
        return new ca.ulaval.glo4003.trotti.interfaces.rest.scooter.model.RideCode(
                rideCode.getCode(),
                rideCode.getExpirationTime()
        );
    }

    public static StartRideResponse toStartRideResponse(RideStartResult result) {
        String message = "Ride started successfully with scooter having battery level: " +
                result.getBatteryLevel() + "%";
        return new StartRideResponse(message);
    }

    public static EndRideResponse toEndRideResponse(RideEndResult result) {
        String billingMessage = result.isChargedImmediately()
                ? "Additional fees of $" + result.getAdditionalFeesAmount() + " have been charged to your card."
                : "Additional fees of $" + result.getAdditionalFeesAmount() + " added to your monthly balance.";

        String message = String.format(
                "Ride ended successfully. Duration: %d minutes %d seconds. %s",
                result.getDurationInMinutes(),
                result.getDurationInSeconds(),
                result.getAdditionalFeesAmount().compareTo(java.math.BigDecimal.ZERO) > 0 ? billingMessage : "No additional fees."
        );

        return new EndRideResponse(
                result.getDurationInMinutes(),
                result.getDurationInSeconds(),
                result.getAdditionalFeesAmount(),
                result.isChargedImmediately(),
                result.getMonthlyBalanceAfter().getAmount(),
                message
        );
    }

    public static List<RideHistoryResponse.RideHistoryItem> toRideHistoryItems(List<Ride> rides) {
        return rides.stream()
                .map(ride -> new RideHistoryResponse.RideHistoryItem(
                        ride.getId(),
                        ride.getStartStationId(),
                        ride.getEndStationId(),
                        ride.getStartTime().toString(),
                        ride.getEndTime().toString(),
                        ride.getDurationMinutes(),
                        ride.getDurationSeconds()
                ))
                .collect(Collectors.toList());
    }

    public static TripHistoryResponse toTripHistoryResponse(TripHistoryResult result) {
        List<TripHistoryResponse.TripItem> tripItems = result.getRides().stream()
                .map(ride -> new TripHistoryResponse.TripItem(
                        ride.getId(),
                        ride.getStartStationId(),
                        ride.getEndStationId(),
                        ride.getStartTime().toString(),
                        ride.getEndTime().toString(),
                        ride.getDurationMinutes(),
                        ride.getDurationSeconds()
                ))
                .collect(Collectors.toList());

        TripHistoryResponse.TripSummaryDto summaryDto = new TripHistoryResponse.TripSummaryDto(
                result.getSummary().getTotalTrips(),
                result.getSummary().getTotalDurationMinutes(),
                result.getSummary().getAverageDurationMinutes(),
                result.getSummary().getFavoriteStationId()
        );

        return new TripHistoryResponse(tripItems, summaryDto);
    }
}

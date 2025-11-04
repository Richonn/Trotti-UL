package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.domain.pass.Pass;
import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.pass.exception.NoActivePassException;
import ca.ulaval.glo4003.trotti.domain.ride.*;
import ca.ulaval.glo4003.trotti.domain.ride.exception.RideNotFoundException;
import ca.ulaval.glo4003.trotti.domain.scooter.Scooter;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.station.Station;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.station.exception.SlotNotAvailableException;
import ca.ulaval.glo4003.trotti.domain.station.exception.StationNotFoundException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReturnRideUseCase {
    private static final long BASE_MINUTES_PER_DAY = 30;
    private static final BigDecimal OVERAGE_FEE = BigDecimal.valueOf(5.00);

    private final RideRepository rideRepository;
    private final PassRepository passRepository;
    private final StationRepository stationRepository;
    private final ScooterRepository scooterRepository;
    private final TripRepository tripRepository;

    public ReturnRideUseCase(RideRepository rideRepository, PassRepository passRepository,
                             StationRepository stationRepository, ScooterRepository scooterRepository,
                             TripRepository tripRepository) {
        this.rideRepository = rideRepository;
        this.passRepository = passRepository;
        this.stationRepository = stationRepository;
        this.scooterRepository = scooterRepository;
        this.tripRepository = tripRepository;
    }

    public RideReturnResult execute(String rideId, String endStationId, int slotNumber) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(RideNotFoundException::new);

        if (ride.getStatus() != RideStatus.ACTIVE) {
            throw new IllegalStateException("Ride is not active");
        }

        Station endStation = stationRepository.findById(endStationId)
                .orElseThrow(StationNotFoundException::new);

        boolean slotAvailable = scooterRepository.findByStationIdAndSlotNumber(endStationId, slotNumber).isEmpty();
        if (!slotAvailable) {
            throw new SlotNotAvailableException("Slot " + slotNumber + " is not available at station " + endStationId);
        }

        if (slotNumber < 1 || slotNumber > endStation.getCapacity()) {
            throw new SlotNotAvailableException("Slot number must be between 1 and " + endStation.getCapacity());
        }

        LocalDateTime endTime = LocalDateTime.now();
        ride.end(endTime, endStationId);
        rideRepository.save(ride);

        Scooter scooter = scooterRepository.findById(ride.getScooterId())
                .orElseThrow(() -> new IllegalStateException("Scooter not found"));
        scooter.returnToStation(endStationId, slotNumber);
        scooterRepository.save(scooter);

        Duration duration = ride.getDuration();

        Pass activePass = passRepository.findActivePassForUser(ride.getUserId(), LocalDate.now())
                .orElseThrow(NoActivePassException::new);

        long minutesUsedToday = tripRepository.getTotalMinutesUsedToday(ride.getUserId(), LocalDate.now());

        long dailyQuota = BASE_MINUTES_PER_DAY + activePass.getExtraMinutesPerDay();

        long totalMinutesAfterTrip = minutesUsedToday + duration.toMinutes();

        BigDecimal additionalFees = BigDecimal.ZERO;
        boolean quotaExceeded = false;

        if (totalMinutesAfterTrip > dailyQuota) {
            additionalFees = OVERAGE_FEE;
            quotaExceeded = true;
        }

        Trip trip = Trip.create(
            ride.getUserId(),
            ride.getId(),
            ride.getStartStationId(),
            endStationId,
            ride.getStartTime(),
            endTime,
            additionalFees
        );
        tripRepository.save(trip);

        return new RideReturnResult(
            trip.getId(),
            ride.getStartStationId(),
            endStationId,
            duration,
            additionalFees,
            quotaExceeded
        );
    }
}

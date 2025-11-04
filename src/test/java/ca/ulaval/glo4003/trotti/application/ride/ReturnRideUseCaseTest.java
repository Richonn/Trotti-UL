package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.domain.pass.Pass;
import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.pass.PricingPolicy;
import ca.ulaval.glo4003.trotti.domain.pass.Session;
import ca.ulaval.glo4003.trotti.domain.ride.*;
import ca.ulaval.glo4003.trotti.domain.ride.exception.RideNotFoundException;
import ca.ulaval.glo4003.trotti.domain.scooter.Scooter;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.station.Station;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.station.exception.SlotNotAvailableException;
import ca.ulaval.glo4003.trotti.domain.station.exception.StationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReturnRideUseCaseTest {

    private ReturnRideUseCase returnRideUseCase;
    private RideRepository rideRepository;
    private PassRepository passRepository;
    private StationRepository stationRepository;
    private ScooterRepository scooterRepository;
    private TripRepository tripRepository;

    private String rideId;
    private String userId;
    private String scooterId;
    private String startStationId;
    private String endStationId;
    private Ride ride;
    private Scooter scooter;
    private Station endStation;
    private Pass pass;

    @BeforeEach
    void setUp() {
        rideRepository = mock(RideRepository.class);
        passRepository = mock(PassRepository.class);
        stationRepository = mock(StationRepository.class);
        scooterRepository = mock(ScooterRepository.class);
        tripRepository = mock(TripRepository.class);

        returnRideUseCase = new ReturnRideUseCase(
            rideRepository, passRepository, stationRepository,
            scooterRepository, tripRepository
        );

        rideId = "ride-123";
        userId = "user-123";
        scooterId = "scooter-123";
        startStationId = "station-start";
        endStationId = "station-end";

        ride = Ride.start(userId, scooterId, startStationId, LocalDateTime.now().minusMinutes(10));
        scooter = new Scooter(scooterId, 80.0, null, 0);
        scooter.rentTo(userId);
        endStation = new Station(endStationId, "End Station", 20);

        pass = Pass.purchase(userId, Session.H2026, 20, false, new PricingPolicy());
    }

    @Test
    void givenValidReturn_whenReturningRide_thenSucceeds() {
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(stationRepository.findById(endStationId)).thenReturn(Optional.of(endStation));
        when(scooterRepository.findByStationIdAndSlotNumber(endStationId, 5)).thenReturn(Optional.empty());
        when(scooterRepository.findById(scooterId)).thenReturn(Optional.of(scooter));
        when(passRepository.findActivePassForUser(userId, LocalDate.now())).thenReturn(Optional.of(pass));
        when(tripRepository.getTotalMinutesUsedToday(userId, LocalDate.now())).thenReturn(0L);

        RideReturnResult result = returnRideUseCase.execute(rideId, endStationId, 5);

        assertNotNull(result);
        assertNotNull(result.getTripId());
        assertEquals(startStationId, result.getStartStationId());
        assertEquals(endStationId, result.getEndStationId());
        assertEquals(BigDecimal.ZERO, result.getAdditionalFees());
        assertFalse(result.isQuotaExceeded());

        verify(rideRepository).save(ride);
        verify(scooterRepository).save(scooter);
        verify(tripRepository).save(any(Trip.class));
    }

    @Test
    void givenQuotaExceeded_whenReturningRide_thenChargesAdditionalFee() {
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(stationRepository.findById(endStationId)).thenReturn(Optional.of(endStation));
        when(scooterRepository.findByStationIdAndSlotNumber(endStationId, 5)).thenReturn(Optional.empty());
        when(scooterRepository.findById(scooterId)).thenReturn(Optional.of(scooter));
        when(passRepository.findActivePassForUser(userId, LocalDate.now())).thenReturn(Optional.of(pass));
        when(tripRepository.getTotalMinutesUsedToday(userId, LocalDate.now())).thenReturn(45L);

        RideReturnResult result = returnRideUseCase.execute(rideId, endStationId, 5);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(5.00), result.getAdditionalFees());
        assertTrue(result.isQuotaExceeded());

        verify(tripRepository).save(any(Trip.class));
    }

    @Test
    void givenNonExistentRide_whenReturningRide_thenThrowsRideNotFoundException() {
        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () ->
            returnRideUseCase.execute(rideId, endStationId, 5)
        );
    }

    @Test
    void givenNonExistentStation_whenReturningRide_thenThrowsStationNotFoundException() {
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(stationRepository.findById(endStationId)).thenReturn(Optional.empty());

        assertThrows(StationNotFoundException.class, () ->
            returnRideUseCase.execute(rideId, endStationId, 5)
        );
    }

    @Test
    void givenSlotNotAvailable_whenReturningRide_thenThrowsSlotNotAvailableException() {
        Scooter occupyingScooter = new Scooter("other-scooter", 60.0, endStationId, 5);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(stationRepository.findById(endStationId)).thenReturn(Optional.of(endStation));
        when(scooterRepository.findByStationIdAndSlotNumber(endStationId, 5))
            .thenReturn(Optional.of(occupyingScooter));

        assertThrows(SlotNotAvailableException.class, () ->
            returnRideUseCase.execute(rideId, endStationId, 5)
        );
    }

    @Test
    void givenInvalidSlotNumber_whenReturningRide_thenThrowsSlotNotAvailableException() {
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(stationRepository.findById(endStationId)).thenReturn(Optional.of(endStation));

        assertThrows(SlotNotAvailableException.class, () ->
            returnRideUseCase.execute(rideId, endStationId, 25)
        );
    }

    @Test
    void givenAlreadyCompletedRide_whenReturningRide_thenThrowsIllegalStateException() {
        ride.end(LocalDateTime.now(), endStationId);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        assertThrows(IllegalStateException.class, () ->
            returnRideUseCase.execute(rideId, endStationId, 5)
        );
    }

    @Test
    void givenShortRide_whenReturningRide_thenNoAdditionalFees() {
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(stationRepository.findById(endStationId)).thenReturn(Optional.of(endStation));
        when(scooterRepository.findByStationIdAndSlotNumber(endStationId, 5)).thenReturn(Optional.empty());
        when(scooterRepository.findById(scooterId)).thenReturn(Optional.of(scooter));
        when(passRepository.findActivePassForUser(userId, LocalDate.now())).thenReturn(Optional.of(pass));
        when(tripRepository.getTotalMinutesUsedToday(userId, LocalDate.now())).thenReturn(0L);

        RideReturnResult result = returnRideUseCase.execute(rideId, endStationId, 5);

        assertEquals(BigDecimal.ZERO, result.getAdditionalFees());
        assertFalse(result.isQuotaExceeded());
        assertTrue(result.getDuration().toMinutes() <= 15);
    }
}

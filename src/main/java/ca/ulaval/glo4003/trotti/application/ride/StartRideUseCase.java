package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.pass.exception.NoActivePassException;
import ca.ulaval.glo4003.trotti.domain.ride.*;
import ca.ulaval.glo4003.trotti.domain.ride.exception.ExpiredRideCodeException;
import ca.ulaval.glo4003.trotti.domain.ride.exception.InvalidRideCodeException;
import ca.ulaval.glo4003.trotti.domain.scooter.Scooter;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.station.Station;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.station.StationState;
import ca.ulaval.glo4003.trotti.domain.station.exception.SlotNotAvailableException;
import ca.ulaval.glo4003.trotti.domain.station.exception.StationNotFoundException;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.AccountNotActivatedException;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StartRideUseCase {
    private final UserRepository userRepository;
    private final PassRepository passRepository;
    private final StationRepository stationRepository;
    private final ScooterRepository scooterRepository;
    private final RideRepository rideRepository;
    private final RequestRideCodeUseCase requestRideCodeUseCase;

    public StartRideUseCase(UserRepository userRepository, PassRepository passRepository,
                             StationRepository stationRepository, ScooterRepository scooterRepository,
                             RideRepository rideRepository, RequestRideCodeUseCase requestRideCodeUseCase) {
        this.userRepository = userRepository;
        this.passRepository = passRepository;
        this.stationRepository = stationRepository;
        this.scooterRepository = scooterRepository;
        this.rideRepository = rideRepository;
        this.requestRideCodeUseCase = requestRideCodeUseCase;
    }

    public RideStartResult execute(String userEmail, String stationName, int slotNumber, String inputCode) {
        Email email = new Email(userEmail);
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if (!user.isActivated()) {
            throw new AccountNotActivatedException();
        }

        if (!passRepository.hasActivePassForUser(user.getId(), LocalDate.now()) && user.getRole() != Role.TECHNICIAN) {
            throw new NoActivePassException();
        }

        RideCode userCode = requestRideCodeUseCase.getUserCode(userEmail);
        if (userCode == null || !userCode.matches(inputCode)) {
            throw new InvalidRideCodeException();
        }
        if (userCode.isExpired()) {
            throw new ExpiredRideCodeException();
        }

        Station station = stationRepository.findById(stationName)
                .orElseThrow(StationNotFoundException::new);

        if (station.getState() == StationState.UNDER_MAINTENANCE) {
            throw new SlotNotAvailableException("Station " + stationName + " is under maintenance");
        }

        Scooter scooter = scooterRepository.findByStationIdAndSlotNumber(stationName, slotNumber)
                .orElseThrow(() -> new SlotNotAvailableException("Scooter not found at the given slot"));

        if (scooter.isLowBattery()) {
            throw new SlotNotAvailableException("Scooter battery too low: " + scooter.getBatteryLevel() + "%");
        }

        scooter.rentTo(user.getId());
        scooterRepository.save(scooter);

        Ride ride = Ride.start(user.getId(), scooter.getId(), LocalDateTime.now());
        rideRepository.save(ride);

        return new RideStartResult(ride.getId(), scooter.getBatteryLevel());
    }
}

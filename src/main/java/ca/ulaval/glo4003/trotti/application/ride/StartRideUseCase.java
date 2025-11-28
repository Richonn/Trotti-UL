package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.pass.exception.NoActivePassException;
import ca.ulaval.glo4003.trotti.domain.ride.*;
import ca.ulaval.glo4003.trotti.domain.ride.exception.ExpiredRideCodeException;
import ca.ulaval.glo4003.trotti.domain.ride.exception.InvalidRideCodeException;
import ca.ulaval.glo4003.trotti.domain.scooter.Scooter;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.station.Station;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.station.exception.StationNotFoundException;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.AccountNotActivatedException;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;

public class StartRideUseCase {
    private final UserRepository userRepository;
    private final PassRepository passRepository;
    private final StationRepository stationRepository;
    private final ScooterRepository scooterRepository;
    private final RideRepository rideRepository;
    private final RideCodeRepository rideCodeRepository;
    private final Clock clock;

    public StartRideUseCase(UserRepository userRepository, PassRepository passRepository,
                             StationRepository stationRepository, ScooterRepository scooterRepository,
                             RideRepository rideRepository, RideCodeRepository rideCodeRepository,
                             Clock clock) {
        this.userRepository = userRepository;
        this.passRepository = passRepository;
        this.stationRepository = stationRepository;
        this.scooterRepository = scooterRepository;
        this.rideRepository = rideRepository;
        this.rideCodeRepository = rideCodeRepository;
        this.clock = clock;
    }

    public RideStartResult execute(String userEmail, String stationName, int slotNumber, String inputCode) {
        Email email = new Email(userEmail);
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        Station station = stationRepository.findById(stationName)
                .orElseThrow(StationNotFoundException::new);

        passRepository.findByUserId(user.getId()).forEach(user::addPass);

        Ride ride = user.startRide(
                clock,
                rideCodeRepository,
                inputCode,
                station,
                slotNumber,
                scooterRepository
        );

        scooterRepository.findByStationIdAndSlotNumber(stationName, slotNumber)
                .ifPresent(scooterRepository::save);

        rideRepository.save(ride);

        double batteryLevel = scooterRepository.findById(ride.getScooterId())
                .map(Scooter::getBatteryLevel)
                .orElse(0.0);

        return new RideStartResult(ride.getId(), batteryLevel);
    }
}

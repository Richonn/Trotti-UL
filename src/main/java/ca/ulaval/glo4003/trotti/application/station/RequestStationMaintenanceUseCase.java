package ca.ulaval.glo4003.trotti.application.station;

import java.util.List;

import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.station.Station;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.station.exception.StationNotFoundException;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.InvalidCredentialsException;

public class RequestStationMaintenanceUseCase {

    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final EmailService mailService;

    public RequestStationMaintenanceUseCase(UserRepository userRepository, StationRepository stationRepository,
            EmailService mailService) {
        this.userRepository = userRepository;
        this.stationRepository = stationRepository;
        this.mailService = mailService;
    }

    public void execute(String id, String email) {
        stationRepository.findById(id)
                .orElseThrow(StationNotFoundException::new);

        List<User> technicians = userRepository
                .findAll()
                .stream()
                .filter(u -> u.getRole() == Role.TECHNICIAN)
                .toList();

        technicians.forEach(t -> {
            mailService.sendEmail(t.getEmail().getValue(), String.format("Maintenance sur la station %s", id),
                    String.format("Une nouvelle maintenance a été demandé par %s", email));
        });
    }

}

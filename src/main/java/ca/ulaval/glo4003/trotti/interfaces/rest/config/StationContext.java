package ca.ulaval.glo4003.trotti.interfaces.rest.config;

import ca.ulaval.glo4003.trotti.application.station.EndStationMaintenanceUseCase;
import ca.ulaval.glo4003.trotti.application.station.RequestStationMaintenanceUseCase;
import ca.ulaval.glo4003.trotti.application.station.StartStationMaintenanceUseCase;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.infrastructure.persistence.memory.InMemoryStationRepository;

public class StationContext {
    private final StationRepository stationRepository;
    private final StartStationMaintenanceUseCase startStationMaintenanceUseCase;
    private final EndStationMaintenanceUseCase endStationMaintenanceUseCase;
    private final RequestStationMaintenanceUseCase requestStationMaintenanceUseCase;

    public StationContext(UserRepository userRepository, EmailService emailService, ScooterRepository scooterRepository) {
        this.stationRepository = new InMemoryStationRepository(scooterRepository);

        this.startStationMaintenanceUseCase = new StartStationMaintenanceUseCase(stationRepository);
        this.endStationMaintenanceUseCase = new EndStationMaintenanceUseCase(stationRepository);
        this.requestStationMaintenanceUseCase = new RequestStationMaintenanceUseCase(userRepository, stationRepository, emailService);
    }

    public StationRepository getStationRepository() {
        return stationRepository;
    }

    public StartStationMaintenanceUseCase getStartStationMaintenanceUseCase() {
        return startStationMaintenanceUseCase;
    }

    public EndStationMaintenanceUseCase getEndStationMaintenanceUseCase() {
        return endStationMaintenanceUseCase;
    }

    public RequestStationMaintenanceUseCase getRequestStationMaintenanceUseCase() {
        return requestStationMaintenanceUseCase;
    }
}

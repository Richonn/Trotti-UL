package ca.ulaval.glo4003.trotti.interfaces.rest.config;

import ca.ulaval.glo4003.trotti.application.scooter.EndTransferScooterUseCase;
import ca.ulaval.glo4003.trotti.application.scooter.StartTransferScooterUseCase;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.infrastructure.persistence.memory.InMemoryScooterRepository;

public class ScooterContext {
    private final ScooterRepository scooterRepository;
    private final StartTransferScooterUseCase startTransferScooterUseCase;
    private final EndTransferScooterUseCase endTransferScooterUseCase;

    public ScooterContext() {
        this.scooterRepository = new InMemoryScooterRepository();
        this.startTransferScooterUseCase = null;
        this.endTransferScooterUseCase = null;
    }

    public ScooterContext(StationRepository stationRepository) {
        this.scooterRepository = new InMemoryScooterRepository();

        this.startTransferScooterUseCase = new StartTransferScooterUseCase(stationRepository, scooterRepository);
        this.endTransferScooterUseCase = new EndTransferScooterUseCase(stationRepository, scooterRepository);
    }

    public ScooterRepository getScooterRepository() {
        return scooterRepository;
    }

    public StartTransferScooterUseCase getStartTransferScooterUseCase() {
        return startTransferScooterUseCase;
    }

    public EndTransferScooterUseCase getEndTransferScooterUseCase() {
        return endTransferScooterUseCase;
    }
}

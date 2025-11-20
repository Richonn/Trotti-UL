package ca.ulaval.glo4003.trotti.application.scooter;

import ca.ulaval.glo4003.trotti.domain.scooter.Scooter;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.scooter.exception.ScooterNotFoundException;
import ca.ulaval.glo4003.trotti.domain.station.Station;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.station.StationState;
import ca.ulaval.glo4003.trotti.domain.station.exception.StationNotFoundException;

public class StartTransferScooterUseCase {

    private final StationRepository stationRepository;
    private final ScooterRepository scooterRepository;

    public StartTransferScooterUseCase(StationRepository stationRepository, ScooterRepository scooterRepository) {
        this.stationRepository = stationRepository;
        this.scooterRepository = scooterRepository;
    }

    public String execute(int slotNumber, String stationId) {
        Scooter scooter = scooterRepository.findByStationIdAndSlotNumber(stationId, slotNumber).orElseThrow(
            () -> new ScooterNotFoundException()
        );

        Station station = stationRepository.findById(stationId).orElseThrow(
            () -> new StationNotFoundException()
        );

        if (station.getState() != StationState.UNDER_MAINTENANCE) {
            throw new IllegalArgumentException("Station is not under maintenance");
        }

        scooter.startTransfer();

        scooterRepository.save(scooter);

        return scooter.getId();
    }
}
package ca.ulaval.glo4003.trotti.application.scooter;

import ca.ulaval.glo4003.trotti.domain.scooter.Scooter;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterState;
import ca.ulaval.glo4003.trotti.domain.scooter.exception.ScooterNotFoundException;
import ca.ulaval.glo4003.trotti.domain.station.Station;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.station.StationState;
import ca.ulaval.glo4003.trotti.domain.station.exception.StationNotFoundException;

public class EndTransferScooterUseCase {

    private final StationRepository stationRepository;
    private final ScooterRepository scooterRepository;

    public EndTransferScooterUseCase(StationRepository stationRepository, ScooterRepository scooterRepository) {
        this.stationRepository = stationRepository;
        this.scooterRepository = scooterRepository;
    }

    public void execute(String scooterId, int slotNumber, String stationId) {
        Scooter scooter = scooterRepository.findById(scooterId).orElseThrow(
            () -> new ScooterNotFoundException()
        );

        Station station = stationRepository.findById(stationId).orElseThrow(
            () -> new StationNotFoundException()
        );

        if (station.getState() == StationState.UNDER_MAINTENANCE) {
            throw new IllegalStateException("Cannot transfer scooter to a station under maintenance");
        }

        if (scooterRepository.findByStationIdAndSlotNumber(stationId, slotNumber).isPresent()) {
            throw new IllegalStateException("Slot " + slotNumber + " is not available at station " + stationId);
        }

        if (scooter.getState() != ScooterState.IN_TRANSFER) {
            throw new IllegalStateException("Scooter is not in transfer state");
        }

        scooter.completeTransfer(stationId, slotNumber);
        scooterRepository.save(scooter);
    }
}

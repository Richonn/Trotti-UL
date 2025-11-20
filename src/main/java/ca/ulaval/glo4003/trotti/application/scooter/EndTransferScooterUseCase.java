package ca.ulaval.glo4003.trotti.application.scooter;

import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterState;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.station.StationState;

public class EndTransferScooterUseCase {

    private final StationRepository stationRepository;
    private final ScooterRepository scooterRepository;

    public EndTransferScooterUseCase(StationRepository stationRepository, ScooterRepository scooterRepository) {
        this.stationRepository = stationRepository;
        this.scooterRepository = scooterRepository;
    }

    public void execute(String scooterId, int slotNumber, String stationId) {
        var scooterOptional = scooterRepository.findById(scooterId);
            var scooter = scooterOptional.get();

            var stationOptional = stationRepository.findById(stationId);
            if (stationOptional.isEmpty()) {
                throw new IllegalArgumentException("Station not found");
            }

            var station = stationOptional.get();
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

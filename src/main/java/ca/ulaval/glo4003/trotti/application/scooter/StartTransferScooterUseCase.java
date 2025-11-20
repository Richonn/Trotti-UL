package ca.ulaval.glo4003.trotti.application.scooter;

import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.station.Station;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.station.StationState;

public class StartTransferScooterUseCase {

    private final StationRepository stationRepository;
    private final ScooterRepository scooterRepository;

    public StartTransferScooterUseCase(StationRepository stationRepository, ScooterRepository scooterRepository) {
        this.stationRepository = stationRepository;
        this.scooterRepository = scooterRepository;
    }

    public String execute(int slotNumber, String stationId) {
        var scooterOptional = scooterRepository.findByStationIdAndSlotNumber(stationId, slotNumber);
        if (scooterOptional.isPresent()) {
            var scooter = scooterOptional.get();

            Station station = stationRepository.findById(stationId)
                    .orElseThrow(() -> new IllegalArgumentException("Station not found"));

            if (station.getState() != StationState.UNDER_MAINTENANCE) {
                throw new IllegalArgumentException("Station is not under maintenance");
            }

            scooter.startTransfer();

            scooterRepository.save(scooter);

            return scooter.getId();
        } else {
            throw new IllegalArgumentException("Scooter not found");
        }
    }
}
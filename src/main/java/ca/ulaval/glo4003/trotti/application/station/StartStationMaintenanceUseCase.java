package ca.ulaval.glo4003.trotti.application.station;

import java.util.Optional;

import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.station.Station;

public class StartStationMaintenanceUseCase {

    private final StationRepository stationRepository;

    public StartStationMaintenanceUseCase(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public void execute(String stationId) {
        Optional<Station> station = stationRepository.findById(stationId);
        if (station.isPresent()) {
            station.get().startMaintenance();
        } else {
            throw new IllegalArgumentException("Station not found");
        }
    }
}

package ca.ulaval.glo4003.trotti.application.station;

import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.station.exception.StationNotFoundException;
import ca.ulaval.glo4003.trotti.domain.station.Station;

public class EndStationMaintenanceUseCase {

    private final StationRepository stationRepository;

    public EndStationMaintenanceUseCase(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public void execute(String stationId) {

        Station station = stationRepository.findById(stationId).orElseThrow(
            () -> new StationNotFoundException()
        );
        station.endMaintenance();
        stationRepository.save(station);
    }
}

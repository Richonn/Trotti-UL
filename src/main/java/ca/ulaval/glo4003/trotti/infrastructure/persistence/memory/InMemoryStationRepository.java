package ca.ulaval.glo4003.trotti.infrastructure.persistence.memory;

import ca.ulaval.glo4003.trotti.domain.station.Station;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStationRepository implements StationRepository {
    private final Map<String, Station> stations = new ConcurrentHashMap<>();

    @Override
    public void save(Station station) {
        stations.put(station.getId(), station);
    }

    @Override
    public Optional<Station> findById(String id) {
        return Optional.ofNullable(stations.get(id));
    }

    @Override
    public List<Station> findAll() {
        return new ArrayList<>(stations.values());
    }

    @Override
    public void initialize() {
        save(new Station("VACHON", "Entrée Vachon #1", 15));
        save(new Station("PEPS", "Entrée PEPS #3", 25));
        save(new Station("DESJARDINS", "Centre de service - Desjardins", 20));
        save(new Station("VANDRY", "Cours du vandry", 15));
        save(new Station("MYRAND", "Secteur EST", 12));
        save(new Station("PYRAMIDE", "Secteur Nord - Chemin Ste-foy", 15));
        save(new Station("CASAULT", "Secteur art", 14));
        save(new Station("PLACE_STE_FOY", "Secteur Ouest - Place Ste-foy", 15));
    }
}

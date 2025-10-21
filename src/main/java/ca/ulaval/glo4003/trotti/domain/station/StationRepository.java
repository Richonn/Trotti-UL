package ca.ulaval.glo4003.trotti.domain.station;

import java.util.List;
import java.util.Optional;

public interface StationRepository {
    void save(Station station);

    Optional<Station> findById(String id);

    List<Station> findAll();

    void initialize();
}

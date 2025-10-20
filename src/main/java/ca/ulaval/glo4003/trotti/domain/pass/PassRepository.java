package ca.ulaval.glo4003.trotti.domain.pass;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PassRepository {
    void save(Pass pass);

    Optional<Pass> findById(String id);

    List<Pass> findByUserId(String userId);

    boolean hasActivePassForUser(String userId, LocalDate date);
}

package ca.ulaval.glo4003.trotti.infrastructure.persistence.memory;

import ca.ulaval.glo4003.trotti.domain.pass.Pass;
import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryPassRepository implements PassRepository {
    private final Map<String, Pass> passes = new ConcurrentHashMap<>();

    @Override
    public void save(Pass pass) {
        passes.put(pass.getId(), pass);
    }

    @Override
    public Optional<Pass> findById(String id) {
        return Optional.ofNullable(passes.get(id));
    }

    @Override
    public List<Pass> findByUserId(String userId) {
        return passes.values().stream()
                .filter(pass -> pass.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
}

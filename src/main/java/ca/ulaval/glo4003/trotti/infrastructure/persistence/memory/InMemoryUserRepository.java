package ca.ulaval.glo4003.trotti.infrastructure.persistence.memory;

import ca.ulaval.glo4003.trotti.domain.pass.Pass;
import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.ride.RideCode;
import ca.ulaval.glo4003.trotti.domain.ride.RideCodeRepository;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final PassRepository passRepository;
    private final RideCodeRepository rideCodeRepository;

    public InMemoryUserRepository(PassRepository passRepository, RideCodeRepository rideCodeRepository) {
        this.passRepository = passRepository;
        this.rideCodeRepository = rideCodeRepository;
    }

    @Override
    public void save(User user) {
        users.put(user.getId(), user);
        for (Pass pass : user.getPasses()) {
            passRepository.save(pass);
        }
        user.getCurrentRideCode().ifPresent(rideCode ->
            rideCodeRepository.save(user.getEmail().getValue(), rideCode)
        );
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id))
                .map(this::loadUserAggregates);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .map(this::loadUserAggregates);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public List<User> findAll() {
        return users.values().stream()
                .map(this::loadUserAggregates)
                .toList();
    }

    @Override
    public void deleteAll() {
        users.clear();
    }

    private User loadUserAggregates(User user) {
        List<Pass> passes = passRepository.findByUserId(user.getId());
        passes.forEach(user::addPass);

        rideCodeRepository.findByUserEmail(user.getEmail().getValue())
                .ifPresent(user::setCurrentRideCode);

        return user;
    }
}

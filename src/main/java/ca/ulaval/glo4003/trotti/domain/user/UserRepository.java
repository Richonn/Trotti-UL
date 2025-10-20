package ca.ulaval.glo4003.trotti.domain.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void save(User user);

    Optional<User> findById(String id);

    Optional<User> findByEmail(Email email);

    boolean existsByEmail(Email email);

    List<User> findAll();

    void deleteAll();
}

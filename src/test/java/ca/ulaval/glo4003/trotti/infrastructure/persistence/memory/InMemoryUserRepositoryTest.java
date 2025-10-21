package ca.ulaval.glo4003.trotti.infrastructure.persistence.memory;

import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.Password;
import ca.ulaval.glo4003.trotti.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {

    private InMemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
    }

    @Test
    void givenUser_whenSaving_thenCanRetrieveById() {
        User user = User.register(
            new Email("test@example.com"),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123"
        );

        repository.save(user);
        Optional<User> result = repository.findById(user.getId());

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void givenUser_whenSaving_thenCanRetrieveByEmail() {
        Email email = new Email("test@example.com");
        User user = User.register(
            email,
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123"
        );

        repository.save(user);
        Optional<User> result = repository.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void givenNonExistentId_whenFindingById_thenReturnsEmpty() {
        Optional<User> result = repository.findById("non-existent-id");

        assertTrue(result.isEmpty());
    }

    @Test
    void givenNonExistentEmail_whenFindingByEmail_thenReturnsEmpty() {
        Optional<User> result = repository.findByEmail(new Email("nonexistent@example.com"));

        assertTrue(result.isEmpty());
    }

    @Test
    void givenExistingUser_whenCheckingIfEmailExists_thenReturnsTrue() {
        Email email = new Email("test@example.com");
        User user = User.register(
            email,
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123"
        );
        repository.save(user);

        boolean exists = repository.existsByEmail(email);

        assertTrue(exists);
    }

    @Test
    void givenNonExistentUser_whenCheckingIfEmailExists_thenReturnsFalse() {
        boolean exists = repository.existsByEmail(new Email("nonexistent@example.com"));

        assertFalse(exists);
    }

    @Test
    void givenMultipleUsers_whenFindingAll_thenReturnsAllUsers() {
        User user1 = User.register(
            new Email("test1@example.com"),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123"
        );
        User user2 = User.register(
            new Email("test2@example.com"),
            new Password("ValidPass123!"),
            "Jane Doe",
            "2000-01-15",
            "F",
            "jando456"
        );

        repository.save(user1);
        repository.save(user2);

        List<User> result = repository.findAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
    }

    @Test
    void givenUsers_whenDeletingAll_thenRepositoryIsEmpty() {
        User user = User.register(
            new Email("test@example.com"),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123"
        );
        repository.save(user);

        repository.deleteAll();

        assertTrue(repository.findAll().isEmpty());
        assertFalse(repository.existsByEmail(new Email("test@example.com")));
    }

    @Test
    void givenSameUser_whenSavingTwice_thenUpdatesUser() {
        User user = User.register(
            new Email("test@example.com"),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123"
        );

        repository.save(user);
        user.activate();
        repository.save(user);

        Optional<User> result = repository.findById(user.getId());

        assertTrue(result.isPresent());
        assertTrue(result.get().isActivated());
    }
}

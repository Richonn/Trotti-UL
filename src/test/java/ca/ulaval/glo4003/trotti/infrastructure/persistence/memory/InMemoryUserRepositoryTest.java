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
        // Arrange
        User user = User.register(
            new Email("test@example.com"),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "M",
            "johdo123"
        );

        // Act
        repository.save(user);
        Optional<User> result = repository.findById(user.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void givenUser_whenSaving_thenCanRetrieveByEmail() {
        // Arrange
        Email email = new Email("test@example.com");
        User user = User.register(
            email,
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "M",
            "johdo123"
        );

        // Act
        repository.save(user);
        Optional<User> result = repository.findByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void givenNonExistentId_whenFindingById_thenReturnsEmpty() {
        // Act
        Optional<User> result = repository.findById("non-existent-id");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void givenNonExistentEmail_whenFindingByEmail_thenReturnsEmpty() {
        // Act
        Optional<User> result = repository.findByEmail(new Email("nonexistent@example.com"));

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void givenExistingUser_whenCheckingIfEmailExists_thenReturnsTrue() {
        // Arrange
        Email email = new Email("test@example.com");
        User user = User.register(
            email,
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "M",
            "johdo123"
        );
        repository.save(user);

        // Act
        boolean exists = repository.existsByEmail(email);

        // Assert
        assertTrue(exists);
    }

    @Test
    void givenNonExistentUser_whenCheckingIfEmailExists_thenReturnsFalse() {
        // Act
        boolean exists = repository.existsByEmail(new Email("nonexistent@example.com"));

        // Assert
        assertFalse(exists);
    }

    @Test
    void givenMultipleUsers_whenFindingAll_thenReturnsAllUsers() {
        // Arrange
        User user1 = User.register(
            new Email("test1@example.com"),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "M",
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

        // Act
        List<User> result = repository.findAll();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
    }

    @Test
    void givenUsers_whenDeletingAll_thenRepositoryIsEmpty() {
        // Arrange
        User user = User.register(
            new Email("test@example.com"),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "M",
            "johdo123"
        );
        repository.save(user);

        // Act
        repository.deleteAll();

        // Assert
        assertTrue(repository.findAll().isEmpty());
        assertFalse(repository.existsByEmail(new Email("test@example.com")));
    }

    @Test
    void givenSameUser_whenSavingTwice_thenUpdatesUser() {
        // Arrange
        User user = User.register(
            new Email("test@example.com"),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "M",
            "johdo123"
        );

        // Act
        repository.save(user);
        user.activate();
        repository.save(user);

        Optional<User> result = repository.findById(user.getId());

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().isActivated());
    }
}

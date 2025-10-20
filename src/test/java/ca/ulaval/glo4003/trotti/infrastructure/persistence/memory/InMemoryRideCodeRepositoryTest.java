package ca.ulaval.glo4003.trotti.infrastructure.persistence.memory;

import ca.ulaval.glo4003.trotti.domain.ride.RideCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryRideCodeRepositoryTest {

    private InMemoryRideCodeRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRideCodeRepository();
    }

    @Test
    void givenRideCode_whenSaving_thenCanRetrieveByEmail() {
        String email = "test@example.com";
        RideCode rideCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));

        repository.save(email, rideCode);
        Optional<RideCode> result = repository.findByUserEmail(email);

        assertTrue(result.isPresent());
        assertEquals(rideCode, result.get());
    }

    @Test
    void givenNonExistentEmail_whenFindingByEmail_thenReturnsEmpty() {
        Optional<RideCode> result = repository.findByUserEmail("nonexistent@example.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void givenExistingRideCode_whenSavingNewCodeForSameEmail_thenReplacesCode() {
        String email = "test@example.com";
        RideCode oldCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));
        RideCode newCode = new RideCode("XYZ789", LocalDateTime.now().plusMinutes(1));

        repository.save(email, oldCode);
        repository.save(email, newCode);

        Optional<RideCode> result = repository.findByUserEmail(email);

        assertTrue(result.isPresent());
        assertEquals(newCode, result.get());
        assertNotEquals(oldCode, result.get());
    }

    @Test
    void givenRideCode_whenDeleting_thenCannotRetrieve() {
        String email = "test@example.com";
        RideCode rideCode = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));
        repository.save(email, rideCode);

        repository.delete(email);
        Optional<RideCode> result = repository.findByUserEmail(email);

        assertTrue(result.isEmpty());
    }

    @Test
    void givenNonExistentEmail_whenDeleting_thenDoesNotThrowException() {
        assertDoesNotThrow(() -> repository.delete("nonexistent@example.com"));
    }

    @Test
    void givenMultipleUsers_whenSavingRideCodes_thenEachUserHasOwnCode() {
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";
        RideCode code1 = new RideCode("ABC123", LocalDateTime.now().plusMinutes(1));
        RideCode code2 = new RideCode("XYZ789", LocalDateTime.now().plusMinutes(1));

        repository.save(email1, code1);
        repository.save(email2, code2);

        Optional<RideCode> result1 = repository.findByUserEmail(email1);
        Optional<RideCode> result2 = repository.findByUserEmail(email2);

        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(code1, result1.get());
        assertEquals(code2, result2.get());
    }
}

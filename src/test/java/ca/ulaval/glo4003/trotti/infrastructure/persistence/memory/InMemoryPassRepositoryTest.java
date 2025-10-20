package ca.ulaval.glo4003.trotti.infrastructure.persistence.memory;

import ca.ulaval.glo4003.trotti.domain.pass.Pass;
import ca.ulaval.glo4003.trotti.domain.pass.PricingPolicy;
import ca.ulaval.glo4003.trotti.domain.pass.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryPassRepositoryTest {

    private InMemoryPassRepository repository;
    private PricingPolicy pricingPolicy;
    private String userId;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPassRepository();
        pricingPolicy = new PricingPolicy();
        userId = "user-123";
    }

    @Test
    void givenPass_whenSaving_thenCanRetrieveById() {
        // Arrange
        Pass pass = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);

        // Act
        repository.save(pass);
        Optional<Pass> result = repository.findById(pass.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(pass, result.get());
    }

    @Test
    void givenNonExistentId_whenFindingById_thenReturnsEmpty() {
        // Act
        Optional<Pass> result = repository.findById("non-existent-id");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void givenMultiplePassesForUser_whenFindingByUserId_thenReturnsAllUserPasses() {
        // Arrange
        Pass pass1 = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);
        Pass pass2 = Pass.purchase(userId, Session.H2026, 20, true, pricingPolicy);
        Pass otherUserPass = Pass.purchase("other-user", Session.A2025, 0, false, pricingPolicy);

        repository.save(pass1);
        repository.save(pass2);
        repository.save(otherUserPass);

        // Act
        List<Pass> result = repository.findByUserId(userId);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(pass1));
        assertTrue(result.contains(pass2));
        assertFalse(result.contains(otherUserPass));
    }

    @Test
    void givenNoPassesForUser_whenFindingByUserId_thenReturnsEmptyList() {
        // Act
        List<Pass> result = repository.findByUserId("non-existent-user");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void givenUserWithActivePass_whenCheckingHasActivePass_thenReturnsTrue() {
        // Arrange
        Pass activePass = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);
        repository.save(activePass);

        LocalDate dateInSession = LocalDate.of(2025, 10, 15);

        // Act
        boolean hasActivePass = repository.hasActivePassForUser(userId, dateInSession);

        // Assert
        assertTrue(hasActivePass);
    }

    @Test
    void givenUserWithNoActivePass_whenCheckingHasActivePass_thenReturnsFalse() {
        // Arrange
        Pass pass = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);
        repository.save(pass);

        LocalDate dateOutsideSession = LocalDate.of(2026, 5, 15);

        // Act
        boolean hasActivePass = repository.hasActivePassForUser(userId, dateOutsideSession);

        // Assert
        assertFalse(hasActivePass);
    }

    @Test
    void givenUserWithDeactivatedPass_whenCheckingHasActivePass_thenReturnsFalse() {
        // Arrange
        Pass pass = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);
        pass.deactivate();
        repository.save(pass);

        LocalDate dateInSession = LocalDate.of(2025, 10, 15);

        // Act
        boolean hasActivePass = repository.hasActivePassForUser(userId, dateInSession);

        // Assert
        assertFalse(hasActivePass);
    }

    @Test
    void givenUserWithMultiplePasses_whenOneIsActive_thenHasActivePassReturnsTrue() {
        // Arrange
        Pass expiredPass = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);
        Pass activePass = Pass.purchase(userId, Session.H2026, 0, false, pricingPolicy);

        repository.save(expiredPass);
        repository.save(activePass);

        LocalDate dateInH2026 = LocalDate.of(2026, 2, 15);

        // Act
        boolean hasActivePass = repository.hasActivePassForUser(userId, dateInH2026);

        // Assert
        assertTrue(hasActivePass);
    }

    @Test
    void givenNonExistentUser_whenCheckingHasActivePass_thenReturnsFalse() {
        // Act
        boolean hasActivePass = repository.hasActivePassForUser("non-existent-user", LocalDate.now());

        // Assert
        assertFalse(hasActivePass);
    }
}

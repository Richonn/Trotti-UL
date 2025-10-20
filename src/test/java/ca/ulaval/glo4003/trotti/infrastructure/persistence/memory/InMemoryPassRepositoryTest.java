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
        Pass pass = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);

        repository.save(pass);
        Optional<Pass> result = repository.findById(pass.getId());

        assertTrue(result.isPresent());
        assertEquals(pass, result.get());
    }

    @Test
    void givenNonExistentId_whenFindingById_thenReturnsEmpty() {
        Optional<Pass> result = repository.findById("non-existent-id");

        assertTrue(result.isEmpty());
    }

    @Test
    void givenMultiplePassesForUser_whenFindingByUserId_thenReturnsAllUserPasses() {
        Pass pass1 = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);
        Pass pass2 = Pass.purchase(userId, Session.H2026, 20, true, pricingPolicy);
        Pass otherUserPass = Pass.purchase("other-user", Session.A2025, 0, false, pricingPolicy);

        repository.save(pass1);
        repository.save(pass2);
        repository.save(otherUserPass);

        List<Pass> result = repository.findByUserId(userId);

        assertEquals(2, result.size());
        assertTrue(result.contains(pass1));
        assertTrue(result.contains(pass2));
        assertFalse(result.contains(otherUserPass));
    }

    @Test
    void givenNoPassesForUser_whenFindingByUserId_thenReturnsEmptyList() {
        List<Pass> result = repository.findByUserId("non-existent-user");

        assertTrue(result.isEmpty());
    }

    @Test
    void givenUserWithActivePass_whenCheckingHasActivePass_thenReturnsTrue() {
        Pass activePass = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);
        repository.save(activePass);

        LocalDate dateInSession = LocalDate.of(2025, 10, 15);

        boolean hasActivePass = repository.hasActivePassForUser(userId, dateInSession);

        assertTrue(hasActivePass);
    }

    @Test
    void givenUserWithNoActivePass_whenCheckingHasActivePass_thenReturnsFalse() {
        Pass pass = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);
        repository.save(pass);

        LocalDate dateOutsideSession = LocalDate.of(2026, 5, 15);

        boolean hasActivePass = repository.hasActivePassForUser(userId, dateOutsideSession);

        assertFalse(hasActivePass);
    }

    @Test
    void givenUserWithDeactivatedPass_whenCheckingHasActivePass_thenReturnsFalse() {
        Pass pass = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);
        pass.deactivate();
        repository.save(pass);

        LocalDate dateInSession = LocalDate.of(2025, 10, 15);

        boolean hasActivePass = repository.hasActivePassForUser(userId, dateInSession);

        assertFalse(hasActivePass);
    }

    @Test
    void givenUserWithMultiplePasses_whenOneIsActive_thenHasActivePassReturnsTrue() {
        Pass expiredPass = Pass.purchase(userId, Session.A2025, 0, false, pricingPolicy);
        Pass activePass = Pass.purchase(userId, Session.H2026, 0, false, pricingPolicy);

        repository.save(expiredPass);
        repository.save(activePass);

        LocalDate dateInH2026 = LocalDate.of(2026, 2, 15);

        boolean hasActivePass = repository.hasActivePassForUser(userId, dateInH2026);

        assertTrue(hasActivePass);
    }

    @Test
    void givenNonExistentUser_whenCheckingHasActivePass_thenReturnsFalse() {
        boolean hasActivePass = repository.hasActivePassForUser("non-existent-user", LocalDate.now());

        assertFalse(hasActivePass);
    }
}

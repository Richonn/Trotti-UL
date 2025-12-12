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
        Pass pass = Pass.purchase(userId, Session.generate(2025, "Automne"), 0, false, pricingPolicy);

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
        Pass pass1 = Pass.purchase(userId, Session.generate(2025, "Automne"), 0, false, pricingPolicy);
        Pass pass2 = Pass.purchase(userId, Session.generate(2026, "Hiver"), 20, true, pricingPolicy);
        Pass otherUserPass = Pass.purchase("other-user", Session.generate(2025, "Automne"), 0, false, pricingPolicy);

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
}

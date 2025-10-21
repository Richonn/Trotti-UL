package ca.ulaval.glo4003.trotti.infrastructure.persistence.memory;

import ca.ulaval.glo4003.trotti.domain.scooter.Scooter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryScooterRepositoryTest {

    private InMemoryScooterRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryScooterRepository();
    }

    @Test
    void givenScooter_whenSaving_thenCanRetrieveById() {
        Scooter scooter = new Scooter("scooter-1", 80.0, "VACHON", 5);

        repository.save(scooter);
        Optional<Scooter> result = repository.findById("scooter-1");

        assertTrue(result.isPresent());
        assertEquals(scooter, result.get());
    }

    @Test
    void givenNonExistentId_whenFindingById_thenReturnsEmpty() {
        Optional<Scooter> result = repository.findById("NON-EXISTENT");

        assertFalse(result.isPresent());
    }

    @Test
    void givenExistingScooter_whenSavingWithSameId_thenUpdates() {
        Scooter scooter1 = new Scooter("scooter-1", 80.0, "VACHON", 5);
        Scooter scooter2 = new Scooter("scooter-1", 50.0, "PEPS", 3);

        repository.save(scooter1);
        repository.save(scooter2);
        Optional<Scooter> result = repository.findById("scooter-1");

        assertTrue(result.isPresent());
        assertEquals(scooter2, result.get());
        assertEquals(50.0, result.get().getBatteryLevel());
    }

    @Test
    void givenScootersAtStation_whenFindingByStationId_thenReturnsAllScootersAtStation() {
        Scooter scooter1 = new Scooter("scooter-1", 80.0, "VACHON", 1);
        Scooter scooter2 = new Scooter("scooter-2", 75.0, "VACHON", 2);
        Scooter scooter3 = new Scooter("scooter-3", 90.0, "PEPS", 1);

        repository.save(scooter1);
        repository.save(scooter2);
        repository.save(scooter3);
        List<Scooter> result = repository.findByStationId("VACHON");

        assertEquals(2, result.size());
        assertTrue(result.contains(scooter1));
        assertTrue(result.contains(scooter2));
        assertFalse(result.contains(scooter3));
    }

    @Test
    void givenNoScootersAtStation_whenFindingByStationId_thenReturnsEmptyList() {
        Scooter scooter = new Scooter("scooter-1", 80.0, "VACHON", 1);
        repository.save(scooter);

        List<Scooter> result = repository.findByStationId("PEPS");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void givenScooterAtSpecificSlot_whenFindingByStationAndSlot_thenReturnsScooter() {
        Scooter scooter1 = new Scooter("scooter-1", 80.0, "VACHON", 5);
        Scooter scooter2 = new Scooter("scooter-2", 75.0, "VACHON", 10);

        repository.save(scooter1);
        repository.save(scooter2);
        Optional<Scooter> result = repository.findByStationIdAndSlotNumber("VACHON", 5);

        assertTrue(result.isPresent());
        assertEquals(scooter1, result.get());
    }

    @Test
    void givenNoScooterAtSlot_whenFindingByStationAndSlot_thenReturnsEmpty() {
        Scooter scooter = new Scooter("scooter-1", 80.0, "VACHON", 5);
        repository.save(scooter);

        Optional<Scooter> result = repository.findByStationIdAndSlotNumber("VACHON", 10);

        assertFalse(result.isPresent());
    }

    @Test
    void givenRentedScooter_whenFindingByStationId_thenDoesNotIncludeRentedScooter() {
        Scooter scooter1 = new Scooter("scooter-1", 80.0, "VACHON", 5);
        Scooter scooter2 = new Scooter("scooter-2", 75.0, "VACHON", 10);
        scooter2.rentTo("user-123");

        repository.save(scooter1);
        repository.save(scooter2);
        List<Scooter> result = repository.findByStationId("VACHON");

        assertEquals(1, result.size());
        assertTrue(result.contains(scooter1));
        assertFalse(result.contains(scooter2));
    }

    @Test
    void givenStation_whenInitializingScooters_thenCreates80PercentOfCapacity() {
        repository.initializeScootersForStation("VACHON", 10);
        List<Scooter> result = repository.findByStationId("VACHON");

        assertEquals(8, result.size());
    }

    @Test
    void givenStationWithCapacity25_whenInitializingScooters_thenCreates20Scooters() {
        repository.initializeScootersForStation("PEPS", 25);
        List<Scooter> result = repository.findByStationId("PEPS");

        assertEquals(20, result.size());
    }

    @Test
    void givenInitializedStation_whenCheckingScooterSlots_thenSlotsAreSequential() {
        repository.initializeScootersForStation("VACHON", 10);
        List<Scooter> scooters = repository.findByStationId("VACHON");

        assertEquals(8, scooters.size());
        for (int i = 0; i < 8; i++) {
            int finalI = i;
            assertTrue(scooters.stream().anyMatch(s ->
                s.getSlotNumber().isPresent() && s.getSlotNumber().get() == finalI
            ));
        }
    }
}

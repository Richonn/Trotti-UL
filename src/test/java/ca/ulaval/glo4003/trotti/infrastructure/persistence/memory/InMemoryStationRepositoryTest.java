package ca.ulaval.glo4003.trotti.infrastructure.persistence.memory;

import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryStationRepositoryTest {

    private InMemoryStationRepository repository;
    private ScooterRepository scooterRepository;

    @BeforeEach
    void setUp() {
        scooterRepository = new InMemoryScooterRepository();
        repository = new InMemoryStationRepository(scooterRepository);
    }

    @Test
    void givenStation_whenSaving_thenCanRetrieveById() {
        Station station = new Station("TEST-1", "Test Location", 10);

        repository.save(station);
        Optional<Station> result = repository.findById("TEST-1");

        assertTrue(result.isPresent());
        assertEquals(station, result.get());
    }

    @Test
    void givenNonExistentId_whenFindingById_thenReturnsEmpty() {
        Optional<Station> result = repository.findById("NON-EXISTENT");

        assertFalse(result.isPresent());
    }

    @Test
    void givenExistingStation_whenSavingWithSameId_thenUpdates() {
        Station station1 = new Station("TEST-1", "Location 1", 10);
        Station station2 = new Station("TEST-1", "Location 2", 20);

        repository.save(station1);
        repository.save(station2);
        Optional<Station> result = repository.findById("TEST-1");

        assertTrue(result.isPresent());
        assertEquals(station2, result.get());
        assertEquals("Location 2", result.get().getLocation());
    }

    @Test
    void givenMultipleStations_whenFindingAll_thenReturnsAllStations() {
        Station station1 = new Station("TEST-1", "Location 1", 10);
        Station station2 = new Station("TEST-2", "Location 2", 20);
        Station station3 = new Station("TEST-3", "Location 3", 15);

        repository.save(station1);
        repository.save(station2);
        repository.save(station3);
        List<Station> result = repository.findAll();

        assertEquals(3, result.size());
        assertTrue(result.contains(station1));
        assertTrue(result.contains(station2));
        assertTrue(result.contains(station3));
    }

    @Test
    void givenEmptyRepository_whenFindingAll_thenReturnsEmptyList() {
        List<Station> result = repository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenInitializing_thenCreatesDefaultStations() {
        repository.initialize();
        List<Station> result = repository.findAll();

        assertEquals(8, result.size());
        assertTrue(repository.findById("VACHON").isPresent());
        assertTrue(repository.findById("PEPS").isPresent());
        assertTrue(repository.findById("DESJARDINS").isPresent());
        assertTrue(repository.findById("VANDRY").isPresent());
        assertTrue(repository.findById("MYRAND").isPresent());
        assertTrue(repository.findById("PYRAMIDE").isPresent());
        assertTrue(repository.findById("CASAULT").isPresent());
        assertTrue(repository.findById("PLACE_STE_FOY").isPresent());
    }

    @Test
    void givenInitializedRepository_whenFindingVachonStation_thenHasCorrectDetails() {
        repository.initialize();

        Optional<Station> vachon = repository.findById("VACHON");

        assertTrue(vachon.isPresent());
        assertEquals("VACHON", vachon.get().getId());
        assertEquals("Entrée Vachon #1", vachon.get().getLocation());
        assertEquals(15, vachon.get().getCapacity());
    }
}

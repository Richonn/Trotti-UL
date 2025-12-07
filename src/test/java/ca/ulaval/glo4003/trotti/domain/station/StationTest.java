package ca.ulaval.glo4003.trotti.domain.station;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StationTest {

    @Test
    void givenValidData_whenCreatingStation_thenSucceeds() {
        Station station = new Station("station-1", "Campus Principal", 20);

        assertEquals("station-1", station.getId());
        assertEquals("Campus Principal", station.getLocation());
        assertEquals(20, station.getCapacity());
    }

    @Test
    void givenNullId_whenCreatingStation_thenThrowsException() {
        assertThrows(NullPointerException.class,
            () -> new Station(null, "Campus Principal", 20));
    }

    @Test
    void givenNullLocation_whenCreatingStation_thenThrowsException() {
        assertThrows(NullPointerException.class,
            () -> new Station("station-1", null, 20));
    }

    @Test
    void givenZeroCapacity_whenCreatingStation_thenThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Station("station-1", "Campus Principal", 0));
    }

    @Test
    void givenNegativeCapacity_whenCreatingStation_thenThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Station("station-1", "Campus Principal", -5));
    }

}

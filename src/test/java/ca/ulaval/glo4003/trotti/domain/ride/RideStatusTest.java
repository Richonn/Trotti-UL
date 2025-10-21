package ca.ulaval.glo4003.trotti.domain.ride;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RideStatusTest {

    @Test
    void givenRideStatusEnum_whenGettingValues_thenContainsAllStatuses() {
        RideStatus[] statuses = RideStatus.values();

        assertEquals(2, statuses.length);
        assertTrue(containsStatus(statuses, RideStatus.ACTIVE));
        assertTrue(containsStatus(statuses, RideStatus.COMPLETED));
    }

    @Test
    void givenRideStatusActive_whenComparingWithActive_thenAreEqual() {
        assertEquals(RideStatus.ACTIVE, RideStatus.ACTIVE);
    }

    @Test
    void givenRideStatusActive_whenComparingWithCompleted_thenAreNotEqual() {
        assertNotEquals(RideStatus.ACTIVE, RideStatus.COMPLETED);
    }

    @Test
    void givenRideStatusActive_whenGettingName_thenReturnsACTIVE() {
        String name = RideStatus.ACTIVE.name();

        assertEquals("ACTIVE", name);
    }

    @Test
    void givenRideStatusCompleted_whenGettingName_thenReturnsCOMPLETED() {
        String name = RideStatus.COMPLETED.name();

        assertEquals("COMPLETED", name);
    }

    @Test
    void givenStatusName_whenConvertingFromString_thenReturnsCorrectEnum() {
        RideStatus active = RideStatus.valueOf("ACTIVE");
        RideStatus completed = RideStatus.valueOf("COMPLETED");

        assertEquals(RideStatus.ACTIVE, active);
        assertEquals(RideStatus.COMPLETED, completed);
    }

    private boolean containsStatus(RideStatus[] statuses, RideStatus target) {
        for (RideStatus status : statuses) {
            if (status == target) {
                return true;
            }
        }
        return false;
    }
}

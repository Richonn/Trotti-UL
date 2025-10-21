package ca.ulaval.glo4003.trotti.domain.ride;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RideIdTest {

    @Test
    void givenGenerateFactory_whenCreating_thenGeneratesUniqueId() {
        RideId rideId1 = RideId.generate();
        RideId rideId2 = RideId.generate();

        assertNotNull(rideId1);
        assertNotNull(rideId2);
        assertNotEquals(rideId1, rideId2);
    }

    @Test
    void givenValidUuidString_whenCreatingFromString_thenSucceeds() {
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";

        RideId rideId = RideId.fromString(uuidString);

        assertNotNull(rideId);
        assertEquals(uuidString, rideId.getValue());
    }

    @Test
    void givenInvalidUuidString_whenCreatingFromString_thenThrowsException() {
        String invalidUuid = "not-a-uuid";

        assertThrows(IllegalArgumentException.class, () -> RideId.fromString(invalidUuid));
    }

    @Test
    void givenNullUuidString_whenCreatingFromString_thenThrowsException() {
        assertThrows(NullPointerException.class, () -> RideId.fromString(null));
    }

    @Test
    void givenRideId_whenGettingValue_thenReturnsUuidString() {
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";
        RideId rideId = RideId.fromString(uuidString);

        String value = rideId.getValue();

        assertEquals(uuidString, value);
    }

    @Test
    void givenTwoRideIdsWithSameUuid_whenComparingEquality_thenAreEqual() {
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";
        RideId rideId1 = RideId.fromString(uuidString);
        RideId rideId2 = RideId.fromString(uuidString);

        assertEquals(rideId1, rideId2);
        assertEquals(rideId1.hashCode(), rideId2.hashCode());
    }

    @Test
    void givenTwoRideIdsWithDifferentUuids_whenComparingEquality_thenAreNotEqual() {
        RideId rideId1 = RideId.generate();
        RideId rideId2 = RideId.generate();

        assertNotEquals(rideId1, rideId2);
    }

    @Test
    void givenRideId_whenConvertingToString_thenReturnsUuidString() {
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";
        RideId rideId = RideId.fromString(uuidString);

        String result = rideId.toString();

        assertEquals(uuidString, result);
    }
}

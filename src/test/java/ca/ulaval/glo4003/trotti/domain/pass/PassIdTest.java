package ca.ulaval.glo4003.trotti.domain.pass;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PassIdTest {

    @Test
    void givenGenerateFactory_whenCreating_thenGeneratesUniqueId() {
        PassId passId1 = PassId.generate();
        PassId passId2 = PassId.generate();

        assertNotNull(passId1);
        assertNotNull(passId2);
        assertNotEquals(passId1, passId2);
    }

    @Test
    void givenValidUuidString_whenCreatingFromString_thenSucceeds() {
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";

        PassId passId = PassId.fromString(uuidString);

        assertNotNull(passId);
        assertEquals(uuidString, passId.getValue());
    }

    @Test
    void givenInvalidUuidString_whenCreatingFromString_thenThrowsException() {
        String invalidUuid = "not-a-uuid";

        assertThrows(IllegalArgumentException.class, () -> PassId.fromString(invalidUuid));
    }

    @Test
    void givenNullUuidString_whenCreatingFromString_thenThrowsException() {
        assertThrows(NullPointerException.class, () -> PassId.fromString(null));
    }

    @Test
    void givenPassId_whenGettingValue_thenReturnsUuidString() {
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";
        PassId passId = PassId.fromString(uuidString);

        String value = passId.getValue();

        assertEquals(uuidString, value);
    }

    @Test
    void givenTwoPassIdsWithSameUuid_whenComparingEquality_thenAreEqual() {
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";
        PassId passId1 = PassId.fromString(uuidString);
        PassId passId2 = PassId.fromString(uuidString);

        assertEquals(passId1, passId2);
        assertEquals(passId1.hashCode(), passId2.hashCode());
    }

    @Test
    void givenTwoPassIdsWithDifferentUuids_whenComparingEquality_thenAreNotEqual() {
        PassId passId1 = PassId.generate();
        PassId passId2 = PassId.generate();

        assertNotEquals(passId1, passId2);
    }

    @Test
    void givenPassId_whenConvertingToString_thenReturnsUuidString() {
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";
        PassId passId = PassId.fromString(uuidString);

        String result = passId.toString();

        assertEquals(uuidString, result);
    }
}

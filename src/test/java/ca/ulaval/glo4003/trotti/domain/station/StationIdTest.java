package ca.ulaval.glo4003.trotti.domain.station;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StationIdTest {

    @Test
    void givenValidId_whenCreatingStationId_thenSucceeds() {
        StationId stationId = new StationId("vachon");

        assertEquals("VACHON", stationId.getValue());
    }

    @Test
    void givenLowercaseId_whenCreatingStationId_thenConvertsToUppercase() {
        StationId stationId = new StationId("peps");

        assertEquals("PEPS", stationId.getValue());
    }

    @Test
    void givenMixedCaseId_whenCreatingStationId_thenConvertsToUppercase() {
        StationId stationId = new StationId("VaChOn");

        assertEquals("VACHON", stationId.getValue());
    }

    @Test
    void givenNullId_whenCreatingStationId_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new StationId(null));
    }

    @Test
    void givenEmptyId_whenCreatingStationId_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new StationId(""));
    }

    @Test
    void givenBlankId_whenCreatingStationId_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new StationId("   "));
    }

    @Test
    void givenTwoStationIdsWithSameValue_whenComparingEquality_thenAreEqual() {
        StationId stationId1 = new StationId("vachon");
        StationId stationId2 = new StationId("VACHON");

        assertEquals(stationId1, stationId2);
        assertEquals(stationId1.hashCode(), stationId2.hashCode());
    }

    @Test
    void givenTwoStationIdsWithDifferentValues_whenComparingEquality_thenAreNotEqual() {
        StationId stationId1 = new StationId("vachon");
        StationId stationId2 = new StationId("peps");

        assertNotEquals(stationId1, stationId2);
    }

    @Test
    void givenStationId_whenConvertingToString_thenReturnsValue() {
        StationId stationId = new StationId("desjardins");

        String result = stationId.toString();

        assertEquals("DESJARDINS", result);
    }
}

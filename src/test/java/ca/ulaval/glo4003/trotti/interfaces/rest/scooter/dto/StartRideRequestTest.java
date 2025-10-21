package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StartRideRequestTest {

    @Test
    void givenDefaultConstructor_whenCreating_thenSucceeds() {
        StartRideRequest request = new StartRideRequest();

        assertNotNull(request);
    }

    @Test
    void givenCode_whenSettingAndGetting_thenReturnsCorrectValue() {
        StartRideRequest request = new StartRideRequest();
        String expectedCode = "ABC123";

        request.setCode(expectedCode);

        assertEquals(expectedCode, request.getCode());
    }

    @Test
    void givenStation_whenSettingAndGetting_thenReturnsCorrectValue() {
        StartRideRequest request = new StartRideRequest();
        String expectedStation = "VACHON";

        request.setStation(expectedStation);

        assertEquals(expectedStation, request.getStation());
    }

    @Test
    void givenSlot_whenSettingAndGetting_thenReturnsCorrectValue() {
        StartRideRequest request = new StartRideRequest();
        String expectedSlot = "5";

        request.setSlot(expectedSlot);

        assertEquals(expectedSlot, request.getSlot());
    }

    @Test
    void givenAllFields_whenSettingAndGetting_thenReturnsAllCorrectValues() {
        StartRideRequest request = new StartRideRequest();

        request.setCode("ABC123");
        request.setStation("VACHON");
        request.setSlot("10");

        assertEquals("ABC123", request.getCode());
        assertEquals("VACHON", request.getStation());
        assertEquals("10", request.getSlot());
    }
}

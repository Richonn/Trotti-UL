package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StartRideResponseTest {

    @Test
    void givenMessage_whenCreatingResponse_thenSucceeds() {
        String message = "Ride started successfully";

        StartRideResponse response = new StartRideResponse(message);

        assertNotNull(response);
        assertEquals(message, response.getMessage());
    }

    @Test
    void givenNullMessage_whenCreatingResponse_thenAcceptsNull() {
        StartRideResponse response = new StartRideResponse(null);

        assertNotNull(response);
        assertNull(response.getMessage());
    }

    @Test
    void givenEmptyMessage_whenCreatingResponse_thenAcceptsEmpty() {
        String message = "";

        StartRideResponse response = new StartRideResponse(message);

        assertEquals(message, response.getMessage());
    }

    @Test
    void givenLongMessage_whenCreatingResponse_thenStoresFullMessage() {
        String message = "Ride started successfully with scooter having battery level: 85.5%";

        StartRideResponse response = new StartRideResponse(message);

        assertEquals(message, response.getMessage());
    }
}

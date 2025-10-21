package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto;

import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.model.RideCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RideCodeResponseTest {

    @Test
    void givenRideCode_whenCreatingResponse_thenSucceeds() {
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);
        RideCode rideCode = new RideCode("123456", expirationTime);

        RideCodeResponse response = new RideCodeResponse(rideCode);

        assertNotNull(response);
        assertEquals("Code sent by email", response.getMessage());
        assertEquals("123456", response.getCode());
        assertEquals(expirationTime.toString(), response.getExpiresAt());
    }

    @Test
    void givenRideCodeWithDifferentCode_whenCreatingResponse_thenReturnsCorrectCode() {
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);
        RideCode rideCode = new RideCode("987654", expirationTime);

        RideCodeResponse response = new RideCodeResponse(rideCode);

        assertEquals("987654", response.getCode());
    }

    @Test
    void givenRideCode_whenGettingMessage_thenReturnsFixedMessage() {
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);
        RideCode rideCode = new RideCode("ABC123", expirationTime);

        RideCodeResponse response = new RideCodeResponse(rideCode);

        assertEquals("Code sent by email", response.getMessage());
    }

    @Test
    void givenRideCode_whenGettingExpiresAt_thenReturnsFormattedTime() {
        LocalDateTime expirationTime = LocalDateTime.of(2025, 10, 20, 14, 30, 45);
        RideCode rideCode = new RideCode("XYZ789", expirationTime);

        RideCodeResponse response = new RideCodeResponse(rideCode);

        assertEquals("2025-10-20T14:30:45", response.getExpiresAt());
    }
}

package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RideCodeTest {

    @Test
    void givenValidData_whenCreatingRideCode_thenSucceeds() {
        String code = "123456";
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);

        RideCode rideCode = new RideCode(code, expirationTime);

        assertNotNull(rideCode);
        assertEquals(code, rideCode.getCode());
        assertEquals(expirationTime, rideCode.getExpirationTime());
    }

    @Test
    void givenFutureExpirationTime_whenCheckingIfExpired_thenReturnsFalse() {
        LocalDateTime futureTime = LocalDateTime.now().plusMinutes(5);
        RideCode rideCode = new RideCode("ABC123", futureTime);

        boolean expired = rideCode.isExpired();

        assertFalse(expired);
    }

    @Test
    void givenPastExpirationTime_whenCheckingIfExpired_thenReturnsTrue() {
        LocalDateTime pastTime = LocalDateTime.now().minusMinutes(5);
        RideCode rideCode = new RideCode("DEF456", pastTime);

        boolean expired = rideCode.isExpired();

        assertTrue(expired);
    }

    @Test
    void givenExactCurrentTime_whenCheckingIfExpired_thenReturnsTrue() {
        LocalDateTime currentTime = LocalDateTime.now();
        RideCode rideCode = new RideCode("GHI789", currentTime);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
        }
        boolean expired = rideCode.isExpired();

        assertTrue(expired);
    }

    @Test
    void givenNullCode_whenCreatingRideCode_thenAcceptsNull() {
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);

        RideCode rideCode = new RideCode(null, expirationTime);

        assertNull(rideCode.getCode());
    }

    @Test
    void givenNullExpirationTime_whenCreatingRideCode_thenAcceptsNull() {
        RideCode rideCode = new RideCode("JKL012", null);

        assertNull(rideCode.getExpirationTime());
    }
}

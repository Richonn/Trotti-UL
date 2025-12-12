package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.response;

import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.response.RideCodeModel;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RideCodeModelTest {

    @Test
    void givenValidData_whenCreatingRideCode_thenSucceeds() {
        String code = "123456";
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);

        RideCodeModel rideCode = new RideCodeModel(code, expirationTime);

        assertNotNull(rideCode);
        assertEquals(code, rideCode.getCode());
        assertEquals(expirationTime, rideCode.getExpirationTime());
    }

    @Test
    void givenFutureExpirationTime_whenCheckingIfExpired_thenReturnsFalse() {
        LocalDateTime futureTime = LocalDateTime.now().plusMinutes(5);
        RideCodeModel rideCode = new RideCodeModel("ABC123", futureTime);

        boolean expired = rideCode.isExpired();

        assertFalse(expired);
    }

    @Test
    void givenPastExpirationTime_whenCheckingIfExpired_thenReturnsTrue() {
        LocalDateTime pastTime = LocalDateTime.now().minusMinutes(5);
        RideCodeModel rideCode = new RideCodeModel("DEF456", pastTime);

        boolean expired = rideCode.isExpired();

        assertTrue(expired);
    }

    @Test
    void givenExactCurrentTime_whenCheckingIfExpired_thenReturnsTrue() {
        LocalDateTime currentTime = LocalDateTime.now();
        RideCodeModel rideCode = new RideCodeModel("GHI789", currentTime);

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

        RideCodeModel rideCode = new RideCodeModel(null, expirationTime);

        assertNull(rideCode.getCode());
    }

    @Test
    void givenNullExpirationTime_whenCreatingRideCode_thenAcceptsNull() {
        RideCodeModel rideCode = new RideCodeModel("JKL012", null);

        assertNull(rideCode.getExpirationTime());
    }
}

package ca.ulaval.glo4003.trotti.domain.ride;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RideCodeGeneratorTest {

    private RideCodeGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new RideCodeGenerator();
    }

    @Test
    void whenGeneratingCode_thenReturnsValidRideCode() {
        RideCode rideCode = generator.generate();

        assertNotNull(rideCode);
        assertNotNull(rideCode.getCode());
        assertNotNull(rideCode.getExpirationTime());
    }

    @Test
    void whenGeneratingCode_thenCodeIsNumeric() {
        RideCode rideCode = generator.generate();

        assertTrue(rideCode.getCode().matches("\\d+"),
            "Code should only contain digits");
    }

    @Test
    void whenGeneratingCode_thenCodeLengthIsWithinExpectedRange() {
        RideCode rideCode = generator.generate();

        int length = rideCode.getCode().length();
        assertTrue(length >= 4 && length <= 6,
            "Code length should be between 4 and 6");
    }

    @Test
    void whenGeneratingCode_thenExpirationTimeIs60SecondsInFuture() {
        LocalDateTime before = LocalDateTime.now();

        RideCode rideCode = generator.generate();

        LocalDateTime after = LocalDateTime.now();
        LocalDateTime expirationTime = rideCode.getExpirationTime();

        assertTrue(expirationTime.isAfter(before.plusSeconds(59)));
        assertTrue(expirationTime.isBefore(after.plusSeconds(61)));
    }

    @Test
    void whenGeneratingMultipleCodes_thenEachCodeIsDifferent() {
        RideCode code1 = generator.generate();
        RideCode code2 = generator.generate();
        RideCode code3 = generator.generate();

        boolean allDifferent = !code1.getCode().equals(code2.getCode()) ||
                               !code2.getCode().equals(code3.getCode());
        assertTrue(allDifferent, "Generated codes should vary");
    }

    @Test
    void whenGeneratingCode_thenCodeIsNotEmpty() {
        RideCode rideCode = generator.generate();

        assertFalse(rideCode.getCode().isEmpty());
    }

    @Test
    void whenGeneratingCode_thenRideCodeIsNotExpiredImmediately() {
        RideCode rideCode = generator.generate();

        assertFalse(rideCode.isExpired(),
            "Newly generated code should not be expired");
    }
}

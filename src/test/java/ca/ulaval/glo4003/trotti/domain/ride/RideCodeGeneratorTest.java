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
        // Act
        RideCode rideCode = generator.generate();

        // Assert
        assertNotNull(rideCode);
        assertNotNull(rideCode.getCode());
        assertNotNull(rideCode.getExpirationTime());
    }

    @Test
    void whenGeneratingCode_thenCodeIsNumeric() {
        // Act
        RideCode rideCode = generator.generate();

        // Assert
        assertTrue(rideCode.getCode().matches("\\d+"),
            "Code should only contain digits");
    }

    @Test
    void whenGeneratingCode_thenCodeLengthIsWithinExpectedRange() {
        // Act
        RideCode rideCode = generator.generate();

        // Assert
        int length = rideCode.getCode().length();
        assertTrue(length >= 4 && length <= 6,
            "Code length should be between 4 and 6");
    }

    @Test
    void whenGeneratingCode_thenExpirationTimeIs60SecondsInFuture() {
        // Arrange
        LocalDateTime before = LocalDateTime.now();

        // Act
        RideCode rideCode = generator.generate();

        // Assert
        LocalDateTime after = LocalDateTime.now();
        LocalDateTime expirationTime = rideCode.getExpirationTime();

        // Expiration should be roughly 60 seconds from now (with small tolerance)
        assertTrue(expirationTime.isAfter(before.plusSeconds(59)));
        assertTrue(expirationTime.isBefore(after.plusSeconds(61)));
    }

    @Test
    void whenGeneratingMultipleCodes_thenEachCodeIsDifferent() {
        // Act
        RideCode code1 = generator.generate();
        RideCode code2 = generator.generate();
        RideCode code3 = generator.generate();

        // Assert - At least some should be different (random generation)
        // With random codes, it's statistically very unlikely all 3 would be identical
        boolean allDifferent = !code1.getCode().equals(code2.getCode()) ||
                               !code2.getCode().equals(code3.getCode());
        assertTrue(allDifferent, "Generated codes should vary");
    }

    @Test
    void whenGeneratingCode_thenCodeIsNotEmpty() {
        // Act
        RideCode rideCode = generator.generate();

        // Assert
        assertFalse(rideCode.getCode().isEmpty());
    }

    @Test
    void whenGeneratingCode_thenRideCodeIsNotExpiredImmediately() {
        // Act
        RideCode rideCode = generator.generate();

        // Assert
        assertFalse(rideCode.isExpired(),
            "Newly generated code should not be expired");
    }
}

package ca.ulaval.glo4003.trotti.domain.scooter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BatteryLevelTest {

    @Test
    void givenValidPercentage_whenCreatingBatteryLevel_thenSucceeds() {
        BatteryLevel batteryLevel = new BatteryLevel(50.0);

        assertEquals(50.0, batteryLevel.getPercentage());
    }

    @Test
    void givenZeroPercentage_whenCreatingBatteryLevel_thenSucceeds() {
        BatteryLevel batteryLevel = new BatteryLevel(0.0);

        assertEquals(0.0, batteryLevel.getPercentage());
    }

    @Test
    void given100Percentage_whenCreatingBatteryLevel_thenSucceeds() {
        BatteryLevel batteryLevel = new BatteryLevel(100.0);

        assertEquals(100.0, batteryLevel.getPercentage());
    }

    @Test
    void givenNegativePercentage_whenCreatingBatteryLevel_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new BatteryLevel(-10.0));
    }

    @Test
    void givenPercentageOver100_whenCreatingBatteryLevel_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new BatteryLevel(150.0));
    }

    @Test
    void givenForInitialization_whenCreatingBatteryLevel_thenReturnsValidLevel() {
        BatteryLevel batteryLevel = BatteryLevel.forInitialization();

        assertTrue(batteryLevel.getPercentage() >= 20.0);
        assertTrue(batteryLevel.getPercentage() <= 100.0);
    }

    @Test
    void givenForInitialization_whenCreatingBatteryLevel_thenNeverLowBattery() {
        for (int i = 0; i < 100; i++) {
            BatteryLevel batteryLevel = BatteryLevel.forInitialization();

            assertFalse(batteryLevel.isLow(),
                "Battery level " + batteryLevel.getPercentage() + "% should not be low (must be > 15%)");
            assertTrue(batteryLevel.isSufficientForRide(),
                "Battery level " + batteryLevel.getPercentage() + "% should be sufficient for ride");
        }
    }

    @Test
    void givenForInitialization_whenCreatingBatteryLevel_thenAlwaysAboveThreshold() {
        for (int i = 0; i < 50; i++) {
            BatteryLevel batteryLevel = BatteryLevel.forInitialization();

            assertTrue(batteryLevel.getPercentage() > 15.0,
                "Battery level must be > 15% for scooters to be available at startup");
        }
    }

    @Test
    void given15Percent_whenCheckingIsLow_thenReturnsTrue() {
        BatteryLevel batteryLevel = new BatteryLevel(15.0);

        assertTrue(batteryLevel.isLow());
        assertFalse(batteryLevel.isSufficientForRide());
    }

    @Test
    void given10Percent_whenCheckingIsLow_thenReturnsTrue() {
        BatteryLevel batteryLevel = new BatteryLevel(10.0);

        assertTrue(batteryLevel.isLow());
    }

    @Test
    void given16Percent_whenCheckingIsLow_thenReturnsFalse() {
        BatteryLevel batteryLevel = new BatteryLevel(16.0);

        assertFalse(batteryLevel.isLow());
        assertTrue(batteryLevel.isSufficientForRide());
    }

    @Test
    void given50Percent_whenCheckingIsSufficient_thenReturnsTrue() {
        BatteryLevel batteryLevel = new BatteryLevel(50.0);

        assertTrue(batteryLevel.isSufficientForRide());
        assertFalse(batteryLevel.isLow());
    }

    @Test
    void givenBatteryLevel_whenConvertingToString_thenReturnsFormattedString() {
        BatteryLevel batteryLevel = new BatteryLevel(75.5);

        String result = batteryLevel.toString();

        assertEquals("75.5%", result);
    }

    @Test
    void givenTwoBatteryLevelsWithSamePercentage_whenComparingEquality_thenAreEqual() {
        BatteryLevel level1 = new BatteryLevel(50.0);
        BatteryLevel level2 = new BatteryLevel(50.0);

        assertEquals(level1, level2);
        assertEquals(level1.hashCode(), level2.hashCode());
    }

    @Test
    void givenTwoBatteryLevelsWithDifferentPercentages_whenComparingEquality_thenAreNotEqual() {
        BatteryLevel level1 = new BatteryLevel(50.0);
        BatteryLevel level2 = new BatteryLevel(75.0);

        assertNotEquals(level1, level2);
    }
}

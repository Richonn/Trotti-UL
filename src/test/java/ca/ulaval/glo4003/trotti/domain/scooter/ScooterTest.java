package ca.ulaval.glo4003.trotti.domain.scooter;

import ca.ulaval.glo4003.trotti.domain.scooter.exception.LowBatteryException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScooterTest {

    @Test
    void givenValidData_whenCreatingScooter_thenSucceeds() {
        Scooter scooter = new Scooter("scooter-123", 80.0, "station-1", 5);

        assertEquals("scooter-123", scooter.getId());
        assertEquals(80.0, scooter.getBatteryLevel());
        assertTrue(scooter.getCurrentStationId().isPresent());
        assertEquals("station-1", scooter.getCurrentStationId().get());
        assertTrue(scooter.getSlotNumber().isPresent());
        assertEquals(5, scooter.getSlotNumber().get());
        assertFalse(scooter.getRentedByUserId().isPresent());
    }

    @Test
    void givenFactoryMethod_whenCreatingScooter_thenSucceeds() {
        Scooter scooter = Scooter.create("station-1", 5);

        assertNotNull(scooter.getId());
        assertTrue(scooter.getBatteryLevel() >= 0 && scooter.getBatteryLevel() <= 100);
    }

    @Test
    void givenInvalidBatteryLevel_whenCreatingScooter_thenThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Scooter("scooter-123", -10, "station-1", 5));
        assertThrows(IllegalArgumentException.class,
            () -> new Scooter("scooter-123", 150, "station-1", 5));
    }

    @Test
    void givenScooterWithHighBattery_whenCheckingAvailability_thenIsAvailable() {
        Scooter scooter = new Scooter("scooter-123", 80.0, "station-1", 5);

        assertTrue(scooter.isAvailable());
        assertFalse(scooter.isLowBattery());
    }

    @Test
    void givenScooterWithLowBattery_whenCheckingAvailability_thenIsNotAvailable() {
        Scooter scooter = new Scooter("scooter-123", 10.0, "station-1", 5);

        assertFalse(scooter.isAvailable());
        assertTrue(scooter.isLowBattery());
    }

    @Test
    void givenScooterAt15Percent_whenCheckingLowBattery_thenIsLowBattery() {
        Scooter scooter = new Scooter("scooter-123", 15.0, "station-1", 5);

        assertTrue(scooter.isLowBattery());
    }

    @Test
    void givenAvailableScooter_whenRenting_thenScooterIsRented() {
        Scooter scooter = new Scooter("scooter-123", 80.0, "station-1", 5);

        scooter.rentTo("user-123");

        assertFalse(scooter.isAvailable());
        assertTrue(scooter.getRentedByUserId().isPresent());
        assertEquals("user-123", scooter.getRentedByUserId().get());
        assertFalse(scooter.getCurrentStationId().isPresent());
        assertFalse(scooter.getSlotNumber().isPresent());
    }

    @Test
    void givenScooterWithLowBattery_whenRenting_thenThrowsLowBatteryException() {
        Scooter scooter = new Scooter("scooter-123", 10.0, "station-1", 5);

        assertThrows(LowBatteryException.class, () -> scooter.rentTo("user-123"));
    }

    @Test
    void givenAlreadyRentedScooter_whenRentingAgain_thenThrowsException() {
        Scooter scooter = new Scooter("scooter-123", 80.0, "station-1", 5);
        scooter.rentTo("user-123");

        assertThrows(IllegalStateException.class, () -> scooter.rentTo("user-456"));
    }

    @Test
    void givenRentedScooter_whenReturningToStation_thenScooterIsAvailable() {
        Scooter scooter = new Scooter("scooter-123", 80.0, "station-1", 5);
        scooter.rentTo("user-123");

        scooter.returnToStation("station-2", 3);

        assertTrue(scooter.isAvailable());
        assertFalse(scooter.getRentedByUserId().isPresent());
        assertTrue(scooter.getCurrentStationId().isPresent());
        assertEquals("station-2", scooter.getCurrentStationId().get());
        assertEquals(3, scooter.getSlotNumber().get());
    }

    @Test
    void givenScooter_whenSettingValidBatteryLevel_thenSucceeds() {
        Scooter scooter = new Scooter("scooter-123", 80.0, "station-1", 5);

        scooter.setBatteryLevel(50.0);

        assertEquals(50.0, scooter.getBatteryLevel());
    }

    @Test
    void givenScooter_whenSettingInvalidBatteryLevel_thenThrowsException() {
        Scooter scooter = new Scooter("scooter-123", 80.0, "station-1", 5);

        assertThrows(IllegalArgumentException.class, () -> scooter.setBatteryLevel(-10));
        assertThrows(IllegalArgumentException.class, () -> scooter.setBatteryLevel(150));
    }

    @Test
    void givenTwoScootersWithSameId_whenComparingEquality_thenAreEqual() {
        Scooter scooter1 = new Scooter("scooter-123", 80.0, "station-1", 5);
        Scooter scooter2 = new Scooter("scooter-123", 50.0, "station-2", 3);

        assertEquals(scooter1, scooter2);
        assertEquals(scooter1.hashCode(), scooter2.hashCode());
    }
}

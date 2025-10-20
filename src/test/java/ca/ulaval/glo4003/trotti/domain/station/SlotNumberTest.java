package ca.ulaval.glo4003.trotti.domain.station;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SlotNumberTest {

    @Test
    void givenValidSlotNumber_whenCreating_thenSucceeds() {
        SlotNumber slotNumber = new SlotNumber(5);

        assertEquals(5, slotNumber.getValue());
    }

    @Test
    void givenZeroSlotNumber_whenCreating_thenSucceeds() {
        SlotNumber slotNumber = new SlotNumber(0);

        assertEquals(0, slotNumber.getValue());
    }

    @Test
    void givenLargeSlotNumber_whenCreating_thenSucceeds() {
        SlotNumber slotNumber = new SlotNumber(99);

        assertEquals(99, slotNumber.getValue());
    }

    @Test
    void givenNegativeSlotNumber_whenCreating_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new SlotNumber(-1));
    }

    @Test
    void givenTwoSlotNumbersWithSameValue_whenComparingEquality_thenAreEqual() {
        SlotNumber slot1 = new SlotNumber(10);
        SlotNumber slot2 = new SlotNumber(10);

        assertEquals(slot1, slot2);
        assertEquals(slot1.hashCode(), slot2.hashCode());
    }

    @Test
    void givenTwoSlotNumbersWithDifferentValues_whenComparingEquality_thenAreNotEqual() {
        SlotNumber slot1 = new SlotNumber(10);
        SlotNumber slot2 = new SlotNumber(20);

        assertNotEquals(slot1, slot2);
    }

    @Test
    void givenSlotNumber_whenConvertingToString_thenReturnsValueAsString() {
        SlotNumber slotNumber = new SlotNumber(42);

        String result = slotNumber.toString();

        assertEquals("42", result);
    }
}

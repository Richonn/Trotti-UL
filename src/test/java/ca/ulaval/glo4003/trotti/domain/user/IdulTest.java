package ca.ulaval.glo4003.trotti.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdulTest {

    @Test
    void givenValidIdul_whenCreatingIdul_thenSucceeds() {
        Idul idul = new Idul("alber123");

        assertEquals("alber123", idul.getValue());
    }

    @Test
    void givenIdulWithUppercase_whenCreatingIdul_thenConvertsToLowercase() {
        Idul idul = new Idul("ALBER123");

        assertEquals("alber123", idul.getValue());
    }

    @Test
    void givenIdulWithMixedCase_whenCreatingIdul_thenConvertsToLowercase() {
        Idul idul = new Idul("AlBeR123");

        assertEquals("alber123", idul.getValue());
    }

    @Test
    void givenEmptyIdul_whenCreatingIdul_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Idul(""));
        assertThrows(IllegalArgumentException.class, () -> new Idul("   "));
    }

    @Test
    void givenNullIdul_whenCreatingIdul_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Idul(null));
    }

    @Test
    void givenTwoIdulsWithSameValue_whenComparingEquality_thenAreEqual() {
        Idul idul1 = new Idul("alber123");
        Idul idul2 = new Idul("alber123");

        assertEquals(idul1, idul2);
        assertEquals(idul1.hashCode(), idul2.hashCode());
    }

    @Test
    void givenTwoIdulsWithDifferentCase_whenComparingEquality_thenAreEqual() {
        Idul idul1 = new Idul("ALBER123");
        Idul idul2 = new Idul("alber123");

        assertEquals(idul1, idul2);
        assertEquals(idul1.hashCode(), idul2.hashCode());
    }

    @Test
    void givenTwoIdulsWithDifferentValues_whenComparingEquality_thenAreNotEqual() {
        Idul idul1 = new Idul("alber123");
        Idul idul2 = new Idul("testi456");

        assertNotEquals(idul1, idul2);
    }

    @Test
    void givenValidIdul_whenConvertingToString_thenReturnsValue() {
        Idul idul = new Idul("alber123");

        assertEquals("alber123", idul.toString());
    }
}

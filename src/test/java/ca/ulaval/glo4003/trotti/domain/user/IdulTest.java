package ca.ulaval.glo4003.trotti.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdulTest {

    @Test
    void givenValidIdul_whenCreatingIdul_thenSucceeds() {
        // Arrange & Act
        Idul idul = new Idul("alber123");

        // Assert
        assertEquals("alber123", idul.getValue());
    }

    @Test
    void givenIdulWithUppercase_whenCreatingIdul_thenConvertsToLowercase() {
        // Arrange & Act
        Idul idul = new Idul("ALBER123");

        // Assert
        assertEquals("alber123", idul.getValue());
    }

    @Test
    void givenIdulWithMixedCase_whenCreatingIdul_thenConvertsToLowercase() {
        // Arrange & Act
        Idul idul = new Idul("AlBeR123");

        // Assert
        assertEquals("alber123", idul.getValue());
    }

    @Test
    void givenEmptyIdul_whenCreatingIdul_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Idul(""));
        assertThrows(IllegalArgumentException.class, () -> new Idul("   "));
    }

    @Test
    void givenNullIdul_whenCreatingIdul_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Idul(null));
    }

    @Test
    void givenTwoIdulsWithSameValue_whenComparingEquality_thenAreEqual() {
        // Arrange
        Idul idul1 = new Idul("alber123");
        Idul idul2 = new Idul("alber123");

        // Act & Assert
        assertEquals(idul1, idul2);
        assertEquals(idul1.hashCode(), idul2.hashCode());
    }

    @Test
    void givenTwoIdulsWithDifferentCase_whenComparingEquality_thenAreEqual() {
        // Arrange
        Idul idul1 = new Idul("ALBER123");
        Idul idul2 = new Idul("alber123");

        // Act & Assert
        assertEquals(idul1, idul2);
        assertEquals(idul1.hashCode(), idul2.hashCode());
    }

    @Test
    void givenTwoIdulsWithDifferentValues_whenComparingEquality_thenAreNotEqual() {
        // Arrange
        Idul idul1 = new Idul("alber123");
        Idul idul2 = new Idul("testi456");

        // Act & Assert
        assertNotEquals(idul1, idul2);
    }

    @Test
    void givenValidIdul_whenConvertingToString_thenReturnsValue() {
        // Arrange
        Idul idul = new Idul("alber123");

        // Act & Assert
        assertEquals("alber123", idul.toString());
    }
}

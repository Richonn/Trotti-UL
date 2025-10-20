package ca.ulaval.glo4003.trotti.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenderTest {

    @Test
    void givenMaleCode_whenCreatingGenderFromString_thenReturnsMale() {
        // Act
        Gender gender = Gender.fromString("M");

        // Assert
        assertEquals(Gender.MALE, gender);
    }

    @Test
    void givenFemaleCode_whenCreatingGenderFromString_thenReturnsFemale() {
        // Act
        Gender gender = Gender.fromString("F");

        // Assert
        assertEquals(Gender.FEMALE, gender);
    }

    @Test
    void givenOtherCode_whenCreatingGenderFromString_thenReturnsOther() {
        // Act
        Gender gender = Gender.fromString("O");

        // Assert
        assertEquals(Gender.OTHER, gender);
    }

    @Test
    void givenPreferNotToSayCode_whenCreatingGenderFromString_thenReturnsPreferNotToSay() {
        // Act
        Gender gender = Gender.fromString("N");

        // Assert
        assertEquals(Gender.PREFER_NOT_TO_SAY, gender);
    }

    @Test
    void givenFullName_whenCreatingGenderFromString_thenReturnsCorrectGender() {
        // Act & Assert
        assertEquals(Gender.MALE, Gender.fromString("MALE"));
        assertEquals(Gender.FEMALE, Gender.fromString("FEMALE"));
        assertEquals(Gender.OTHER, Gender.fromString("OTHER"));
        assertEquals(Gender.PREFER_NOT_TO_SAY, Gender.fromString("PREFER_NOT_TO_SAY"));
    }

    @Test
    void givenLowercaseInput_whenCreatingGenderFromString_thenIsCaseInsensitive() {
        // Act & Assert
        assertEquals(Gender.MALE, Gender.fromString("m"));
        assertEquals(Gender.FEMALE, Gender.fromString("f"));
        assertEquals(Gender.MALE, Gender.fromString("male"));
        assertEquals(Gender.FEMALE, Gender.fromString("female"));
    }

    @Test
    void givenInvalidGender_whenCreatingGenderFromString_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> Gender.fromString("INVALID"));
    }

    @Test
    void givenEmptyString_whenCreatingGenderFromString_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> Gender.fromString(""));
        assertThrows(IllegalArgumentException.class, () -> Gender.fromString("   "));
    }

    @Test
    void givenNull_whenCreatingGenderFromString_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> Gender.fromString(null));
    }

    @Test
    void givenGender_whenGettingCode_thenReturnsCorrectCode() {
        // Act & Assert
        assertEquals("M", Gender.MALE.getCode());
        assertEquals("F", Gender.FEMALE.getCode());
        assertEquals("O", Gender.OTHER.getCode());
        assertEquals("N", Gender.PREFER_NOT_TO_SAY.getCode());
    }
}

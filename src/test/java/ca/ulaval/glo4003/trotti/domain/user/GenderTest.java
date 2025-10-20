package ca.ulaval.glo4003.trotti.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenderTest {

    @Test
    void givenMaleCode_whenCreatingGenderFromString_thenReturnsMale() {
        Gender gender = Gender.fromString("H");

        assertEquals(Gender.HOMME, gender);
    }

    @Test
    void givenFemaleCode_whenCreatingGenderFromString_thenReturnsFemale() {
        Gender gender = Gender.fromString("F");

        assertEquals(Gender.FEMME, gender);
    }

    @Test
    void givenOtherCode_whenCreatingGenderFromString_thenReturnsOther() {
        Gender gender = Gender.fromString("A");

        assertEquals(Gender.AUTRE, gender);
    }

    @Test
    void givenPreferNotToSayCode_whenCreatingGenderFromString_thenReturnsPreferNotToSay() {
        Gender gender = Gender.fromString("N");

        assertEquals(Gender.PREFER_NOT_TO_SAY, gender);
    }

    @Test
    void givenFullName_whenCreatingGenderFromString_thenReturnsCorrectGender() {
        assertEquals(Gender.HOMME, Gender.fromString("H"));
        assertEquals(Gender.FEMME, Gender.fromString("F"));
        assertEquals(Gender.AUTRE, Gender.fromString("A"));
        assertEquals(Gender.PREFER_NOT_TO_SAY, Gender.fromString("PREFER_NOT_TO_SAY"));
    }

    @Test
    void givenLowercaseInput_whenCreatingGenderFromString_thenIsCaseInsensitive() {
        assertEquals(Gender.HOMME, Gender.fromString("h"));
        assertEquals(Gender.FEMME, Gender.fromString("f"));
        assertEquals(Gender.HOMME, Gender.fromString("homme"));
        assertEquals(Gender.FEMME, Gender.fromString("femme"));
    }

    @Test
    void givenInvalidGender_whenCreatingGenderFromString_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> Gender.fromString("INVALID"));
    }

    @Test
    void givenEmptyString_whenCreatingGenderFromString_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> Gender.fromString(""));
        assertThrows(IllegalArgumentException.class, () -> Gender.fromString("   "));
    }

    @Test
    void givenNull_whenCreatingGenderFromString_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> Gender.fromString(null));
    }

    @Test
    void givenGender_whenGettingCode_thenReturnsCorrectCode() {
        assertEquals("H", Gender.HOMME.getCode());
        assertEquals("F", Gender.FEMME.getCode());
        assertEquals("A", Gender.AUTRE.getCode());
        assertEquals("N", Gender.PREFER_NOT_TO_SAY.getCode());
    }
}

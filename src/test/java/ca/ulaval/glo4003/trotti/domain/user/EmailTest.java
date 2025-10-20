package ca.ulaval.glo4003.trotti.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void givenValidEmail_whenCreatingEmail_thenSucceeds() {
        Email email = new Email("test@example.com");

        assertEquals("test@example.com", email.getValue());
    }

    @Test
    void givenInvalidEmail_whenCreatingEmail_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Email("invalid-email"));
        assertThrows(IllegalArgumentException.class, () -> new Email(""));
        assertThrows(IllegalArgumentException.class, () -> new Email(null));
    }

    @Test
    void givenTwoEmailsWithSameValue_whenComparingEquality_thenAreEqual() {
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("test@example.com");

        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    void givenTwoEmailsWithDifferentValues_whenComparingEquality_thenAreNotEqual() {
        Email email1 = new Email("test1@example.com");
        Email email2 = new Email("test2@example.com");

        assertNotEquals(email1, email2);
    }
}

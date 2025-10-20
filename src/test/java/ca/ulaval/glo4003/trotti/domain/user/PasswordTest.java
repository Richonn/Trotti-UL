package ca.ulaval.glo4003.trotti.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordTest {

    @Test
    void givenValidPassword_whenCreatingPassword_thenSucceeds() {
        // Arrange & Act
        Password password = new Password("ValidPass123!");

        // Assert
        assertEquals("ValidPass123!", password.getValue());
    }

    @Test
    void givenPasswordWithoutUppercase_whenCreatingPassword_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Password("invalidpass123!"));
    }

    @Test
    void givenPasswordWithoutNumber_whenCreatingPassword_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Password("InvalidPass!"));
    }

    @Test
    void givenPasswordWithoutSpecialCharacter_whenCreatingPassword_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Password("InvalidPass123"));
    }

    @Test
    void givenPasswordTooShort_whenCreatingPassword_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Password("Pass1!"));
    }

    @Test
    void givenEmptyPassword_whenCreatingPassword_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Password(""));
        assertThrows(IllegalArgumentException.class, () -> new Password("   "));
    }

    @Test
    void givenNullPassword_whenCreatingPassword_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Password(null));
    }

    @Test
    void givenValidPassword_whenMatchingWithSamePassword_thenReturnsTrue() {
        // Arrange
        Password password = new Password("ValidPass123!");

        // Act & Assert
        assertTrue(password.matches("ValidPass123!"));
    }

    @Test
    void givenValidPassword_whenMatchingWithDifferentPassword_thenReturnsFalse() {
        // Arrange
        Password password = new Password("ValidPass123!");

        // Act & Assert
        assertFalse(password.matches("DifferentPass456!"));
    }

    @Test
    void givenTwoPasswordsWithSameValue_whenComparingEquality_thenAreEqual() {
        // Arrange
        Password password1 = new Password("ValidPass123!");
        Password password2 = new Password("ValidPass123!");

        // Act & Assert
        assertEquals(password1, password2);
        assertEquals(password1.hashCode(), password2.hashCode());
    }

    @Test
    void givenTwoPasswordsWithDifferentValues_whenComparingEquality_thenAreNotEqual() {
        // Arrange
        Password password1 = new Password("ValidPass123!");
        Password password2 = new Password("DifferentPass456!");

        // Act & Assert
        assertNotEquals(password1, password2);
    }
}

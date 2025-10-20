package ca.ulaval.glo4003.trotti.domain.user;

import ca.ulaval.glo4003.trotti.domain.user.exception.AccountAlreadyActivatedException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void givenValidData_whenRegisteringUser_thenSucceeds() {
        // Arrange & Act
        User user = User.register(
                new Email("test@example.com"),
                new Password("ValidPass123!"),
                "John Doe",
                "2000-01-15",
                "M",
                "johdo123"
        );

        // Assert
        assertNotNull(user.getId());
        assertEquals("test@example.com", user.getEmail().getValue());
        assertEquals("John Doe", user.getName());
        assertEquals(LocalDate.of(2000, 1, 15), user.getDateOfBirth());
        assertEquals(Gender.MALE, user.getGender());
        assertEquals("johdo123", user.getIdul().getValue());
        assertFalse(user.isActivated());
    }

    @Test
    void givenNewUser_whenActivating_thenAccountBecomesActivated() {
        // Arrange
        User user = User.register(
                new Email("test@example.com"),
                new Password("ValidPass123!"),
                "John Doe",
                "2000-01-15",
                "M",
                "johdo123"
        );

        // Act
        user.activate();

        // Assert
        assertTrue(user.isActivated());
    }

    @Test
    void givenAlreadyActivatedUser_whenActivatingAgain_thenThrowsException() {
        // Arrange
        User user = User.register(
                new Email("test@example.com"),
                new Password("ValidPass123!"),
                "John Doe",
                "2000-01-15",
                "M",
                "johdo123"
        );
        user.activate();

        // Act & Assert
        assertThrows(AccountAlreadyActivatedException.class, user::activate);
    }

    @Test
    void givenActivatedUser_whenCheckingLoginWithCorrectPassword_thenReturnsTrue() {
        // Arrange
        User user = User.register(
                new Email("test@example.com"),
                new Password("ValidPass123!"),
                "John Doe",
                "2000-01-15",
                "M",
                "johdo123"
        );
        user.activate();

        // Act & Assert
        assertTrue(user.canLogin("ValidPass123!"));
    }

    @Test
    void givenActivatedUser_whenCheckingLoginWithWrongPassword_thenReturnsFalse() {
        // Arrange
        User user = User.register(
                new Email("test@example.com"),
                new Password("ValidPass123!"),
                "John Doe",
                "2000-01-15",
                "M",
                "johdo123"
        );
        user.activate();

        // Act & Assert
        assertFalse(user.canLogin("WrongPassword456!"));
    }

    @Test
    void givenNonActivatedUser_whenCheckingLogin_thenReturnsFalse() {
        // Arrange
        User user = User.register(
                new Email("test@example.com"),
                new Password("ValidPass123!"),
                "John Doe",
                "2000-01-15",
                "M",
                "johdo123"
        );

        // Act & Assert - Even with correct password, non-activated user cannot login
        assertFalse(user.canLogin("ValidPass123!"));
    }

    @Test
    void givenUserWithDateOfBirth_whenGettingAge_thenReturnsCorrectAge() {
        // Arrange
        LocalDate birthDate = LocalDate.now().minusYears(25).minusMonths(3);
        User user = User.register(
                new Email("test@example.com"),
                new Password("ValidPass123!"),
                "John Doe",
                birthDate.toString(),
                "M",
                "johdo123"
        );

        // Act
        int age = user.getAge();

        // Assert
        assertEquals(25, age);
    }

    @Test
    void givenTwoUsersWithDifferentIds_whenComparingEquality_thenAreNotEqual() {
        // Arrange
        User user1 = User.register(
                new Email("test1@example.com"),
                new Password("ValidPass123!"),
                "John Doe",
                "2000-01-15",
                "M",
                "johdo123"
        );
        User user2 = User.register(
                new Email("test2@example.com"),
                new Password("ValidPass123!"),
                "Jane Doe",
                "2000-01-15",
                "F",
                "jando456"
        );

        // Act & Assert
        assertNotEquals(user1, user2);
    }
}

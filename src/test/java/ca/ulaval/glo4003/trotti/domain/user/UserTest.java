package ca.ulaval.glo4003.trotti.domain.user;

import ca.ulaval.glo4003.trotti.domain.user.exception.AccountAlreadyActivatedException;
import ca.ulaval.glo4003.trotti.infrastructure.time.FixedClock;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void givenValidData_whenRegisteringUser_thenSucceeds() {
        User user = User.register(
                new Email("test@example.com"),
                new Password("ValidPass123!"),
                "John Doe",
                "2000-01-15",
                "H",
                "johdo123",
                Role.USER
        );

        assertNotNull(user.getId());
        assertEquals("test@example.com", user.getEmail().getValue());
        assertEquals("John Doe", user.getName());
        assertEquals(LocalDate.of(2000, 1, 15), user.getDateOfBirth());
        assertEquals(Gender.HOMME, user.getGender());
        assertEquals("johdo123", user.getIdul().getValue());
        assertFalse(user.isActivated());
    }

    @Test
    void givenNewUser_whenActivating_thenAccountBecomesActivated() {
        User user = User.register(
                new Email("test@example.com"),
                new Password("ValidPass123!"),
                "John Doe",
                "2000-01-15",
                "H",
                "johdo123",
                Role.USER
        );

        user.activate();

        assertTrue(user.isActivated());
    }

    @Test
    void givenAlreadyActivatedUser_whenActivatingAgain_thenThrowsException() {
        User user = User.register(
                new Email("test@example.com"),
                new Password("ValidPass123!"),
                "John Doe",
                "2000-01-15",
                "H",
                "johdo123",
                Role.USER
        );
        user.activate();

        assertThrows(AccountAlreadyActivatedException.class, user::activate);
    }

    @Test
    void givenActivatedUser_whenCheckingLoginWithCorrectPassword_thenReturnsTrue() {
        User user = User.register(
                new Email("test@example.com"),
                new Password("ValidPass123!"),
                "John Doe",
                "2000-01-15",
                "H",
                "johdo123",
                Role.USER
        );
        user.activate();

        assertTrue(user.canLogin("ValidPass123!"));
    }

    @Test
    void givenActivatedUser_whenCheckingLoginWithWrongPassword_thenReturnsFalse() {
        User user = User.register(
                new Email("test@example.com"),
                new Password("ValidPass123!"),
                "John Doe",
                "2000-01-15",
                "H",
                "johdo123",
                Role.USER
        );
        user.activate();

        assertFalse(user.canLogin("WrongPassword456!"));
    }

    @Test
    void givenNonActivatedUser_whenCheckingLogin_thenReturnsFalse() {
        User user = User.register(
                new Email("test@example.com"),
                new Password("ValidPass123!"),
                "John Doe",
                "2000-01-15",
                "H",
                "johdo123",
                Role.USER
        );

        assertFalse(user.canLogin("ValidPass123!"));
    }

    @Test
    void givenUserWithDateOfBirth_whenGettingAge_thenReturnsCorrectAge() {
        LocalDate today = LocalDate.of(2024, 4, 15);
        LocalDate birthDate = today.minusYears(25).minusMonths(3);
        FixedClock clock = new FixedClock(today);
        User user = User.register(
                new Email("test@example.com"),
                new Password("ValidPass123!"),
                "John Doe",
                birthDate.toString(),
                "H",
                "johdo123",
                Role.USER
        );

        int age = user.getAge(clock);

        assertEquals(25, age);
    }

    @Test
    void givenTwoUsersWithDifferentIds_whenComparingEquality_thenAreNotEqual() {
        User user1 = User.register(
                new Email("test1@example.com"),
                new Password("ValidPass123!"),
                "John Doe",
                "2000-01-15",
                "H",
                "johdo123",
                Role.USER
        );
        User user2 = User.register(
                new Email("test2@example.com"),
                new Password("ValidPass123!"),
                "Jane Doe",
                "2000-01-15",
                "F",
                "jando456",
                Role.USER
        );

        assertNotEquals(user1, user2);
    }
}

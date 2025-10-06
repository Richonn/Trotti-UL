package ca.ulaval.glo4003.trotti.user;

import ca.ulaval.glo4003.trotti.user.model.User;
import ca.ulaval.glo4003.trotti.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;
    private User validUser;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        userService.clearAllUsers();
        validUser = new User();
        validUser.setName("Alexandra Dupont");
        validUser.setDateOfBirth("2000-01-01");
        validUser.setGender("F");
        validUser.setAge(25);
        validUser.setIdul("dupaa12");
        validUser.setEmail("Alice@Email.com");
        validUser.setPassword("Password@123");
    }

    @Test
    void register_validUser_shouldSucceed() {
        userService.register(validUser);

        assertEquals(validUser, userService.findByEmail("Alice@Email.com"));
    }

    @Test
    void register_missingFields_shouldThrow() {
        User incomplete = new User();
        incomplete.setName(null);
        incomplete.setEmail(null);
        incomplete.setPassword(null);
        incomplete.setGender(null);
        incomplete.setDateOfBirth(null);
        incomplete.setAge(null);
        incomplete.setIdul("bad");
        assertThrows(RuntimeException.class, () -> userService.register(incomplete));
    }

    @Test
    void register_invalidPassword_shouldThrow() {
        User invalidPass = new User();
        invalidPass.setName("Bob");
        invalidPass.setGender("M");
        invalidPass.setDateOfBirth("1999-01-01");
        invalidPass.setAge(26);
        invalidPass.setIdul("bob1");
        invalidPass.setEmail("bob@email.com");
        invalidPass.setPassword("short");
        assertThrows(RuntimeException.class, () -> userService.register(invalidPass));
    }

    @Test
    void login_validCredentials_shouldReturnUser() {
        userService.register(validUser);

        User logged = userService.login("Alice@Email.com", "Password@123");
        assertNotNull(logged);
    }

    @Test
    void login_invalidPassword_shouldThrow() {
        userService.register(validUser);

        assertThrows(RuntimeException.class,
                () -> userService.login("Alice@Email.com", "WrongPass1!"));
    }

    @Test
    void getAllUsers_shouldContainRegisteredUser() {
        userService.register(validUser);

        assertTrue(userService.getAllUsers().contains(validUser));
    }

    @Test
    void register_alreadyUsedEmail_shouldThrow() {
        User newUser = new User();
        newUser.setName("John Doe");
        newUser.setDateOfBirth("2000-01-01");
        newUser.setGender("M");
        newUser.setAge(20);
        newUser.setIdul("jodoe24");
        newUser.setEmail("Alice@Email.com");
        newUser.setPassword("Password@321!");

        userService.register(validUser);
        assertThrows(RuntimeException.class, () -> userService.register(newUser));
    }

    @Test
    void register_notAlreadyUsedEmail_shouldNotThrow() {
        User newUser = new User();
        newUser.setName("John Doe");
        newUser.setDateOfBirth("2000-01-01");
        newUser.setGender("M");
        newUser.setAge(20);
        newUser.setIdul("jodoe24");
        newUser.setEmail("John@Email.com");
        newUser.setPassword("Password@321!");

        userService.register(validUser);
        assertDoesNotThrow(() -> userService.register(newUser));
    }
}

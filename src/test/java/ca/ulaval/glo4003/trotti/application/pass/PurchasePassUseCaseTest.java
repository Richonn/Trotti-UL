package ca.ulaval.glo4003.trotti.application.pass;

import ca.ulaval.glo4003.trotti.domain.pass.*;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.Password;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;
import ca.ulaval.glo4003.trotti.infrastructure.time.FixedClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurchasePassUseCaseTest {

    private PurchasePassUseCase purchasePassUseCase;
    private UserRepository userRepository;
    private PricingPolicy pricingPolicy;
    private Clock clock;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        pricingPolicy = new PricingPolicy();
        clock = new FixedClock(LocalDate.of(2024, 1, 15));
        ca.ulaval.glo4003.trotti.domain.pass.PassFactory passFactory = new ca.ulaval.glo4003.trotti.domain.pass.PassFactory();
        purchasePassUseCase = new PurchasePassUseCase(userRepository, pricingPolicy, clock, passFactory);
    }

    @Test
    void givenValidData_whenExecuting_thenCreatesAndSavesPass() {
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123",
            Role.USER
        );

        String sessionCode = "A2025";
        int extraMinutes = 20;
        boolean monthlyBilling = true;
        String cardNumber = "4532015112830366";
        String cardHolder = "John Doe";
        String expiryDate = "12/25";
        String cvv = "123";

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        PassPurchaseResult result = purchasePassUseCase.execute(
            email, sessionCode, extraMinutes, monthlyBilling,
            cardNumber, cardHolder, expiryDate, cvv
        );

        assertNotNull(result);
        assertEquals(sessionCode, result.getSessionCode());
        assertEquals(0, BigDecimal.valueOf(61).compareTo(result.getTotalPrice()));
        assertEquals(monthlyBilling, result.isMonthlyBilling());

        verify(userRepository).save(user);
    }

    @Test
    void givenNonExistentUser_whenExecuting_thenThrowsUserNotFoundException() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
            purchasePassUseCase.execute(
                email, "A2025", 0, false,
                "4532015112830366", "John Doe", "12/25", "123"
            )
        );
    }

    @Test
    void givenInvalidSession_whenExecuting_thenThrowsIllegalArgumentException() {
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123",
            Role.USER

        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () ->
            purchasePassUseCase.execute(
                email, "INVALID", 0, false,
                "4532015112830366", "John Doe", "12/25", "123"
            )
        );
    }

    @Test
    void givenInvalidCardNumber_whenExecuting_thenThrowsIllegalArgumentException() {
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123",
            Role.USER
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () ->
            purchasePassUseCase.execute(
                email, "A2025", 0, false,
                "1234567890123456", "John Doe", "12/25", "123"
            )
        );
    }

    @Test
    void givenInvalidExtraMinutes_whenExecuting_thenThrowsIllegalArgumentException() {
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123",
            Role.USER
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () ->
            purchasePassUseCase.execute(
                email, "A2025", 15, false,
                "4532015112830366", "John Doe", "12/25", "123"
            )
        );
    }

    @Test
    void givenZeroExtraMinutes_whenExecuting_thenChargesBasePrice() {
        String email = "test@example.com";
        User user = User.register(
            new Email(email),
            new Password("ValidPass123!"),
            "John Doe",
            "2000-01-15",
            "H",
            "johdo123",
            Role.USER
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        PassPurchaseResult result = purchasePassUseCase.execute(
            email, "A2025", 0, false,
            "4532015112830366", "John Doe", "12/25", "123"
        );

        assertEquals(0, BigDecimal.valueOf(45).compareTo(result.getTotalPrice()));
    }
}

package ca.ulaval.glo4003.trotti.application.pass;

import ca.ulaval.glo4003.trotti.application.pass.dto.PassPurchaseResult;
import ca.ulaval.glo4003.trotti.domain.pass.*;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.shared.TransactionFailedException;
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

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurchasePassUseCaseTest {

    private PurchasePassUseCase purchasePassUseCase;
    private UserRepository userRepository;
    private PricingPolicy pricingPolicy;
    private PaymentService paymentService;
    private Clock clock;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        pricingPolicy = new PricingPolicy();
        paymentService = mock(PaymentService.class);
        clock = new FixedClock(LocalDate.of(2024, 1, 15));
        ca.ulaval.glo4003.trotti.domain.pass.PassFactory passFactory = new ca.ulaval.glo4003.trotti.domain.pass.PassFactory();
        purchasePassUseCase = new PurchasePassUseCase(userRepository, pricingPolicy, clock, passFactory, paymentService);
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

    @Test
    void givenMonthlyBilling_whenExecuting_thenDoesNotChargePayment() {
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
            email, "A2025", 0, true,
            "4532015112830366", "John Doe", "12/25", "123"
        );

        verify(paymentService, never()).chargePayment(any(), any());
        verify(userRepository).save(user);
        assertThat(user.getMonthlyBalance().getAmount().getAmount().doubleValue()).isGreaterThan(0);
    }

    @Test
    void givenDirectBilling_whenExecuting_thenChargesPayment() {
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

        verify(paymentService, times(1)).chargePayment(any(PaymentInfo.class), any(Money.class));
        verify(userRepository).save(user);
    }

    @Test
    void givenTransactionFailure_whenExecuting_thenRollsBackPassCreation() {
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
        doThrow(new TransactionFailedException("Payment gateway error"))
            .when(paymentService).chargePayment(any(PaymentInfo.class), any(Money.class));

        int initialPassCount = user.getPasses().size();

        assertThatThrownBy(() ->
            purchasePassUseCase.execute(
                email, "A2025", 0, false,
                "4532015112830366", "John Doe", "12/25", "123"
            )
        ).isInstanceOf(TransactionFailedException.class)
         .hasMessageContaining("Payment gateway error");

        assertThat(user.getPasses()).hasSize(initialPassCount);
        verify(userRepository, never()).save(user);
    }
}

package ca.ulaval.glo4003.trotti.application.billing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import ca.ulaval.glo4003.trotti.application.user.dto.MonthlyInvoiceDto;
import ca.ulaval.glo4003.trotti.domain.pass.Money;
import ca.ulaval.glo4003.trotti.domain.pass.PaymentInfo;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.BalanceEmptyException;
import ca.ulaval.glo4003.trotti.domain.user.exception.InvalidPaymentInfos;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;

public class PayUserBalanceUseCase {

    private final UserRepository userRepository;
    private final Clock clock;

    public PayUserBalanceUseCase(UserRepository userRepository, Clock clock) {
        this.userRepository = userRepository;
        this.clock = clock;
    }

    public MonthlyInvoiceDto execute(String email) {
        User user = userRepository.findByEmail(new Email(email)).orElseThrow(UserNotFoundException::new);

        if (user.getMonthlyBalance().getAmount().getAmount().equals(new BigDecimal(0)))
            throw new BalanceEmptyException();

        Money amount = user.getMonthlyBalance().getAmount();

        // Handle payment
        PaymentInfo paymentInfo = user.getSavedPaymentInfo().orElseThrow(
                () -> new IllegalArgumentException("No payment information available. Please provide card details."));

        if (paymentInfo.isExpired(clock))
            throw new InvalidPaymentInfos();

        user.resetMonthlyBalance();

        LocalDate current = LocalDate.now();

        Month previousMonth = current.getMonth().minus(1);
        int year = current.getYear();
        if (current.getMonth() == Month.JANUARY)
            year -= 1;
        int lastDayOfMonth = 0;
        if (previousMonth == Month.FEBRUARY) {
            if (year % 4 == 0)
                lastDayOfMonth = 29;
            else
                lastDayOfMonth = 28;
        } else {
            if (previousMonth.getValue() % 2 == 0)
                lastDayOfMonth = 31;
            else
                lastDayOfMonth = 30;
        }

        return new MonthlyInvoiceDto(
                amount,
                LocalDate.of(year, previousMonth, 1),
                LocalDate.of(year, previousMonth, lastDayOfMonth));
    }

}

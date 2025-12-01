package ca.ulaval.glo4003.trotti.application.billing;

import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SendPaymentReminderUseCase {
    private final UserRepository userRepository;
    private final EmailService emailService;

    public SendPaymentReminderUseCase(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public void execute() {
        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getMonthlyBalance().getAmount().isZero())
                .toList();

        users.forEach(user -> {
            BigDecimal amount = user.getMonthlyBalance().getAmount().getAmount();
            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.CANADA);
            String formattedAmount = formatter.format(amount);
            emailService.sendEmail(user.getEmail().getValue(), "Sole impayé",
                    String.format("Il vous reste un solde de %s a payé pour le mois dernier", formattedAmount));
        });
    }
}

package ca.ulaval.glo4003.trotti.interfaces.rest.config;

import ca.ulaval.glo4003.trotti.application.billing.AutomaticPaymentUseCase;
import ca.ulaval.glo4003.trotti.application.billing.PayUserBalanceUseCase;
import ca.ulaval.glo4003.trotti.application.billing.SendPaymentReminderUseCase;
import ca.ulaval.glo4003.trotti.application.billing.UpdateAutoPayPreferenceUseCase;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;

public class BillingContext {
    private final PayUserBalanceUseCase payUserBalanceUseCase;
    private final SendPaymentReminderUseCase sendPaymentReminderUseCase;
    private final UpdateAutoPayPreferenceUseCase updateAutoPayPreferenceUseCase;
    private final AutomaticPaymentUseCase automaticPaymentUseCase;

    public BillingContext(UserRepository userRepository, EmailService emailService, Clock clock) {
        this.payUserBalanceUseCase = new PayUserBalanceUseCase(userRepository, clock);
        this.sendPaymentReminderUseCase = new SendPaymentReminderUseCase(userRepository, emailService);
        this.updateAutoPayPreferenceUseCase = new UpdateAutoPayPreferenceUseCase(userRepository);
        this.automaticPaymentUseCase = new AutomaticPaymentUseCase(userRepository);
    }

    public PayUserBalanceUseCase getPayUserBalanceUseCase() {
        return payUserBalanceUseCase;
    }

    public SendPaymentReminderUseCase getSendPaymentReminderUseCase() {
        return sendPaymentReminderUseCase;
    }

    public UpdateAutoPayPreferenceUseCase getUpdateAutoPayPreferenceUseCase() {
        return updateAutoPayPreferenceUseCase;
    }

    public AutomaticPaymentUseCase getAutomaticPaymentUseCase() {
        return automaticPaymentUseCase;
    }
}

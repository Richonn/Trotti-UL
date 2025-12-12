package ca.ulaval.glo4003.trotti.application.pass;

import ca.ulaval.glo4003.trotti.application.pass.dto.PassPurchaseResult;
import ca.ulaval.glo4003.trotti.domain.pass.*;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.shared.TransactionFailedException;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;
import ca.ulaval.glo4003.trotti.domain.pass.Session;

public class PurchasePassUseCase {
    private final UserRepository userRepository;
    private final PricingStrategy pricingStrategy;
    private final Clock clock;
    private final PassFactory passFactory;
    private final PaymentService paymentService;

    public PurchasePassUseCase(UserRepository userRepository, PricingStrategy pricingStrategy, Clock clock, PassFactory passFactory, PaymentService paymentService) {
        this.userRepository = userRepository;
        this.pricingStrategy = pricingStrategy;
        this.clock = clock;
        this.passFactory = passFactory;
        this.paymentService = paymentService;
    }

    public PassPurchaseResult execute(String userEmail, String sessionCode, int extraMinutesPerDay,
                                       boolean monthlyBilling, String cardNumber, String cardHolder,
                                       String expiryDate, String cvv) throws TransactionFailedException {
        Email email = new Email(userEmail);
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        PaymentInfo paymentInfo;
        if (cardNumber != null && !cardNumber.trim().isEmpty()) {
            paymentInfo = new PaymentInfo(cardNumber, cardHolder, expiryDate, cvv);
            if (paymentInfo.isExpired(clock)) {
                throw new IllegalArgumentException("Card is expired");
            }
            user.savePaymentInfo(paymentInfo);
        } else {
            paymentInfo = user.getSavedPaymentInfo()
                    .orElseThrow(() -> new IllegalArgumentException("No payment information available. Please provide card details."));
            if (paymentInfo.isExpired(clock)) {
                throw new IllegalArgumentException("Saved card is expired. Please provide new card details.");
            }
        }

        Session session = Session.getInstance(sessionCode);

        Pass pass = user.purchasePass(session, extraMinutesPerDay, monthlyBilling, pricingStrategy, passFactory);

        if (monthlyBilling) {
            user.addToMonthlyBalance(pass.getTotalPrice(), clock.today());
        } else {
            try {
                paymentService.chargePayment(paymentInfo, pass.getTotalPrice());
            } catch (TransactionFailedException e) {
                user.removePass(pass);
                throw e;
            }
        }

        userRepository.save(user);

        return new PassPurchaseResult(
                pass.getId(),
                session.getId(),
                pass.getTotalPrice().getAmount(),
                monthlyBilling,
                session.getStartDate(),
                session.getEndDate()
        );
    }
}

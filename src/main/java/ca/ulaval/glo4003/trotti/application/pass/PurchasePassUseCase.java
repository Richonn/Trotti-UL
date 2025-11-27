package ca.ulaval.glo4003.trotti.application.pass;

import ca.ulaval.glo4003.trotti.domain.pass.*;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;
import ca.ulaval.glo4003.trotti.domain.pass.Session;

public class PurchasePassUseCase {
    private final UserRepository userRepository;
    private final PassRepository passRepository;
    private final PricingPolicy pricingPolicy;
    private final Clock clock;

    public PurchasePassUseCase(UserRepository userRepository, PassRepository passRepository, PricingPolicy pricingPolicy, Clock clock) {
        this.userRepository = userRepository;
        this.passRepository = passRepository;
        this.pricingPolicy = pricingPolicy;
        this.clock = clock;
    }

    public PassPurchaseResult execute(String userEmail, String sessionCode, int extraMinutesPerDay,
                                       boolean monthlyBilling, String cardNumber, String cardHolder,
                                       String expiryDate, String cvv) {
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

        Pass pass = user.purchasePass(session, extraMinutesPerDay, monthlyBilling, pricingPolicy);
        passRepository.save(pass);
        userRepository.save(user);

        return new PassPurchaseResult(
                pass.getId(),
                session.name(),
                pass.getTotalPrice(),
                monthlyBilling,
                session.getStartDate(),
                session.getEndDate()
        );
    }
}

package ca.ulaval.glo4003.trotti.application.pass;

import ca.ulaval.glo4003.trotti.domain.pass.*;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;
import ca.ulaval.glo4003.trotti.domain.pass.Session;

public class PurchasePassUseCase {
    private final UserRepository userRepository;
    private final PassRepository passRepository;
    private final PricingPolicy pricingPolicy;

    public PurchasePassUseCase(UserRepository userRepository, PassRepository passRepository, PricingPolicy pricingPolicy) {
        this.userRepository = userRepository;
        this.passRepository = passRepository;
        this.pricingPolicy = pricingPolicy;
    }

    public PassPurchaseResult execute(String userEmail, String sessionCode, int extraMinutesPerDay,
                                       boolean monthlyBilling, String cardNumber, String cardHolder,
                                       String expiryDate, String cvv) {
        Email email = new Email(userEmail);
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if (cardNumber != null && !cardNumber.trim().isEmpty()) {
            PaymentInfo paymentInfo = new PaymentInfo(cardNumber, cardHolder, expiryDate, cvv);
            user.savePaymentInfo(paymentInfo);
            userRepository.save(user);
        } else {
            if (user.getSavedPaymentInfo().isEmpty()) {
                throw new IllegalArgumentException("No payment information available. Please provide card details.");
            }
        }

        Session session = Session.getInstance(sessionCode);

        Pass pass = Pass.purchase(user.getId(), session, extraMinutesPerDay, monthlyBilling, pricingPolicy);
        passRepository.save(pass);

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

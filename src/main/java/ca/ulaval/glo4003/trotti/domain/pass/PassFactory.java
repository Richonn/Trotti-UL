package ca.ulaval.glo4003.trotti.domain.pass;

import java.math.BigDecimal;
import java.util.UUID;

public class PassFactory {
    public Pass createForPurchase(String userId, Session session, int extraMinutesPerDay,
                                 boolean monthlyBilling, PricingStrategy pricingStrategy) {
        String passId = UUID.randomUUID().toString();
        Money price = pricingStrategy.calculatePrice(session, extraMinutesPerDay);
        return Pass.create(passId, userId, session, extraMinutesPerDay, monthlyBilling, price);
    }

    public Pass createWithId(String passId, String userId, Session session, int extraMinutesPerDay,
                            boolean monthlyBilling, Money totalPrice) {
        return Pass.create(passId, userId, session, extraMinutesPerDay, monthlyBilling, totalPrice);
    }
}

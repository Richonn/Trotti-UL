package ca.ulaval.glo4003.trotti.domain.pass;

import ca.ulaval.glo4003.trotti.domain.pass.Session;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Pass {
    private final String id;
    private final String userId;
    private final Session session;
    private final int extraMinutesPerDay;
    private final boolean monthlyBilling;
    private final Money totalPrice;
    private boolean active;

    Pass(String id, String userId, Session session, int extraMinutesPerDay,
         boolean monthlyBilling, Money totalPrice) {
        this.id = Objects.requireNonNull(id, "Pass ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.session = Objects.requireNonNull(session, "Session cannot be null");
        this.extraMinutesPerDay = extraMinutesPerDay;
        this.monthlyBilling = monthlyBilling;
        this.totalPrice = Objects.requireNonNull(totalPrice, "Total price cannot be null");
        this.active = true;
    }

    static Pass create(String id, String userId, Session session, int extraMinutesPerDay,
                      boolean monthlyBilling, Money totalPrice) {
        return new Pass(id, userId, session, extraMinutesPerDay, monthlyBilling, totalPrice);
    }

    public static Pass purchase(String userId, Session session, int extraMinutesPerDay,
                                boolean monthlyBilling, PricingStrategy pricingStrategy) {
        PassFactory factory = new PassFactory();
        return factory.createForPurchase(userId, session, extraMinutesPerDay, monthlyBilling, pricingStrategy);
    }

    public boolean isActiveOn(LocalDate date) {
        return active && session.contains(date);
    }

    public void deactivate() {
        this.active = false;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Session getSession() {
        return session;
    }

    public int getExtraMinutesPerDay() {
        return extraMinutesPerDay;
    }

    public boolean isMonthlyBilling() {
        return monthlyBilling;
    }

    public Money getTotalPrice() {
        return totalPrice;
    }

    public boolean isActive() {
        return active;
    }
}

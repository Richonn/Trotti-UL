package ca.ulaval.glo4003.trotti.domain.pass;

public interface PricingStrategy {
    Money calculatePrice(Session session, int extraMinutesPerDay);
}

package ca.ulaval.glo4003.trotti.domain.pass;

import ca.ulaval.glo4003.trotti.domain.pass.Session;

import java.math.BigDecimal;

public class PricingPolicy implements PricingStrategy {
    private static final int MONTHS_PER_SESSION = 4;
    private static final Money DEFAULT_BASE_PRICE = Money.cad(45);
    private static final Money DEFAULT_EXTRA_PRICE_PER_BLOCK = Money.cad(2);

    private final Money basePrice;
    private final Money extraPricePerBlock;

    public PricingPolicy() {
        this(DEFAULT_BASE_PRICE, DEFAULT_EXTRA_PRICE_PER_BLOCK);
    }

    public PricingPolicy(Money basePrice, Money extraPricePerBlock) {
        this.basePrice = basePrice;
        this.extraPricePerBlock = extraPricePerBlock;
    }

    @Override
    public Money calculatePrice(Session session, int extraMinutesPerDay) {
        if (extraMinutesPerDay < 0) {
            throw new IllegalArgumentException("Extra minutes must be greater or equal to 0");
        }
        if (extraMinutesPerDay % 10 != 0) {
            throw new IllegalArgumentException("Extra minutes must be divisible by 10");
        }

        int extraBlocks = extraMinutesPerDay / 10;

        Money extraPrice = extraPricePerBlock
                .multiply(extraBlocks)
                .multiply(BigDecimal.valueOf(MONTHS_PER_SESSION));

        return basePrice.add(extraPrice);
    }
}

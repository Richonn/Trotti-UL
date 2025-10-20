package ca.ulaval.glo4003.trotti.domain.pass;

import ca.ulaval.glo4003.trotti.domain.pass.Session;

import java.math.BigDecimal;

public class PricingPolicy {
    private static final BigDecimal BASE_PRICE = BigDecimal.valueOf(45);
    private static final BigDecimal EXTRA_PRICE_PER_BLOCK = BigDecimal.valueOf(2);
    private static final int MONTHS_PER_SESSION = 4;

    public BigDecimal calculatePrice(Session session, int extraMinutesPerDay) {
        if (extraMinutesPerDay < 0) {
            throw new IllegalArgumentException("Extra minutes must be greater or equal to 0");
        }
        if (extraMinutesPerDay % 10 != 0) {
            throw new IllegalArgumentException("Extra minutes must be divisible by 10");
        }

        int extraBlocks = extraMinutesPerDay / 10;

        BigDecimal extraPrice = EXTRA_PRICE_PER_BLOCK
                .multiply(BigDecimal.valueOf(extraBlocks))
                .multiply(BigDecimal.valueOf(MONTHS_PER_SESSION));

        return BASE_PRICE.add(extraPrice);
    }
}

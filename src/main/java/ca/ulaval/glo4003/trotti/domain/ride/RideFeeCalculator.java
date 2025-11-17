package ca.ulaval.glo4003.trotti.domain.ride;

import ca.ulaval.glo4003.trotti.domain.pass.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

public class RideFeeCalculator {
    private static final int FREE_MINUTES = 15;
    private static final BigDecimal COST_PER_MINUTE = new BigDecimal("0.075");
    private static final int ROUNDING_THRESHOLD_SECONDS = 15;
    private static final BigDecimal QUOTA_EXCEEDED_FEE = new BigDecimal("5.00");

    public Money calculateAdditionalFees(Duration rideDuration) {
        long totalSeconds = rideDuration.getSeconds();
        long totalMinutes = totalSeconds / 60;
        long remainingSeconds = totalSeconds % 60;

        if (totalMinutes < FREE_MINUTES) {
            return Money.zero();
        }

        long billableMinutes = totalMinutes - FREE_MINUTES;

        if (remainingSeconds >= ROUNDING_THRESHOLD_SECONDS) {
            billableMinutes++;
        }

        BigDecimal fee = COST_PER_MINUTE.multiply(BigDecimal.valueOf(billableMinutes));
        fee = fee.setScale(2, RoundingMode.HALF_UP);

        return new Money(fee);
    }

    public Money getQuotaExceededFee() {
        return new Money(QUOTA_EXCEEDED_FEE);
    }
}

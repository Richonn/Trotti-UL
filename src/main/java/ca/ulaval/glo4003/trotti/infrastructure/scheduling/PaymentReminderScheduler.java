package ca.ulaval.glo4003.trotti.infrastructure.scheduling;

import ca.ulaval.glo4003.trotti.application.billing.SendPaymentReminderUseCase;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PaymentReminderScheduler {
    private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    private final SendPaymentReminderUseCase useCase;

    public PaymentReminderScheduler(SendPaymentReminderUseCase useCase) {
        this.useCase = useCase;
    }

    public void start() {
        long initialDelay = computeInitialDelay(6);

        exec.scheduleAtFixedRate(
                this::runIfFirstOfMonth,
                initialDelay,
                TimeUnit.DAYS.toMillis(1),
                TimeUnit.MILLISECONDS
        );
    }

    private void runIfFirstOfMonth() {
        LocalDate today = LocalDate.now();
        if (today.getDayOfMonth() == 1) {
            useCase.execute();
        }
    }

    public void stop() {
        exec.shutdownNow();
    }

    private long computeInitialDelay(int targetHour) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = now.withHour(targetHour).withMinute(0).withSecond(0);

        if (now.isAfter(next)) {
            next = next.plusDays(1);
        }

        return Duration.between(now, next).toMillis();
    }
}

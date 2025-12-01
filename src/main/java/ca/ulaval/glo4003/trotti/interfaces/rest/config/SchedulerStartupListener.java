package ca.ulaval.glo4003.trotti.interfaces.rest.config;

import ca.ulaval.glo4003.trotti.infrastructure.scheduling.PaymentReminderScheduler;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class SchedulerStartupListener implements ServletContextListener {

    private PaymentReminderScheduler scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ApplicationContext ctx = ApplicationContext.getInstance();

        scheduler = new PaymentReminderScheduler(
                ctx.getSendPaymentReminderUseCase()
        );

        scheduler.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) scheduler.stop();
    }
}

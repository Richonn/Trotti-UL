package ca.ulaval.glo4003.trotti.interfaces.rest.config;

import ca.ulaval.glo4003.trotti.infrastructure.scheduling.AutoPaymentScheduler;
import ca.ulaval.glo4003.trotti.infrastructure.scheduling.PaymentReminderScheduler;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class SchedulerStartupListener implements ServletContextListener {

    private PaymentReminderScheduler paymentReminderScheduler;
    private AutoPaymentScheduler  autoPaymentScheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ApplicationContext ctx = ApplicationContext.getInstance();

        autoPaymentScheduler = new AutoPaymentScheduler(
                ctx.getAutomaticPaymentUseCase()
        );
        paymentReminderScheduler = new PaymentReminderScheduler(
                ctx.getSendPaymentReminderUseCase()
        );

        autoPaymentScheduler.start();
        paymentReminderScheduler.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (autoPaymentScheduler != null) autoPaymentScheduler.stop();
        if (paymentReminderScheduler != null) paymentReminderScheduler.stop();
    }
}

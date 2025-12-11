package ca.ulaval.glo4003.trotti.interfaces.rest.config;

import ca.ulaval.glo4003.trotti.application.pass.GetUserPassesUseCase;
import ca.ulaval.glo4003.trotti.application.pass.PurchasePassUseCase;
import ca.ulaval.glo4003.trotti.domain.pass.PassFactory;
import ca.ulaval.glo4003.trotti.domain.pass.PassRepository;
import ca.ulaval.glo4003.trotti.domain.pass.PaymentService;
import ca.ulaval.glo4003.trotti.domain.pass.PricingPolicy;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.shared.TransactionService;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.infrastructure.persistence.memory.InMemoryPassRepository;

public class PassContext {
    private final PassRepository passRepository;
    private final PurchasePassUseCase purchasePassUseCase;
    private final GetUserPassesUseCase getUserPassesUseCase;

    public PassContext(UserRepository userRepository, Clock clock) {
        PricingPolicy pricingPolicy = new PricingPolicy();
        PassFactory passFactory = new PassFactory();
        TransactionService transactionService = new TransactionService();
        PaymentService paymentService = new PaymentService(transactionService);
        this.passRepository = new InMemoryPassRepository();

        this.purchasePassUseCase = new PurchasePassUseCase(userRepository, pricingPolicy, clock, passFactory, paymentService);
        this.getUserPassesUseCase = new GetUserPassesUseCase(userRepository);
    }

    public PassRepository getPassRepository() {
        return passRepository;
    }

    public PurchasePassUseCase getPurchasePassUseCase() {
        return purchasePassUseCase;
    }

    public GetUserPassesUseCase getGetUserPassesUseCase() {
        return getUserPassesUseCase;
    }
}

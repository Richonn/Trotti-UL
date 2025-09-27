package ca.ulaval.glo4003.trotti.pass.service;

import ca.ulaval.glo4003.trotti.pass.dto.InvoiceDto;
import ca.ulaval.glo4003.trotti.pass.dto.PurchaseRequest;
import ca.ulaval.glo4003.trotti.pass.dto.PurchaseResponse;
import ca.ulaval.glo4003.trotti.pass.model.Pass;
import ca.ulaval.glo4003.trotti.pass.model.PaymentInfo;
import ca.ulaval.glo4003.trotti.pass.util.SessionUtil;
import ca.ulaval.glo4003.trotti.user.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PassService {
    private static final BigDecimal BASE_PRICE = BigDecimal.valueOf(45);
    private static final int BASE_INCLUDED_MINUTES = 30;
    private static final BigDecimal EXTRA_PRICE_PER_BLOCK = BigDecimal.valueOf(2);

    public PurchaseResponse purchase(PurchaseRequest request, User user) {
        int months = SessionUtil.getSessionDurationInMonths(request.getSession());

        int extraBlocks = request.getExtraMinutesPerDay() / 10;

        BigDecimal extraPrice = EXTRA_PRICE_PER_BLOCK
                .multiply(BigDecimal.valueOf(extraBlocks))
                .multiply(BigDecimal.valueOf(months));

        BigDecimal totalPrice = BASE_PRICE.add(extraPrice);

        PaymentInfo paymentInfo;

        if (request.getCardNumber() == null || request.getCardNumber().isBlank()) {
            paymentInfo = user.getPaymentInfo();
            if (paymentInfo == null) {
                throw new RuntimeException("No payment info available. Please provide card details.");
            }
        } else {
            paymentInfo = new PaymentInfo(
                    request.getCardNumber(),
                    request.getCardHolder(),
                    request.getExpiryDate(),
                    request.getCvv()
            );
            user.setPaymentInfo(paymentInfo);
        }

        if (!processPayment(paymentInfo, totalPrice)) {
            throw new RuntimeException("Payment failed");
        }

        Pass pass = new Pass(
                request.getSession(),
                request.getExtraMinutesPerDay(),
                request.isMonthlyBilling(),
                totalPrice
        );
        user.addPass(pass);

        LocalDate startDate = SessionUtil.getSessionStartDate(request.getSession());
        LocalDate endDate = SessionUtil.getSessionEndDate(request.getSession());

        InvoiceDto invoice = new InvoiceDto(
                pass.getSession().name(),
                pass.getTotalPrice(),
                pass.isMonthlyBilling(),
                startDate,
                endDate
        );

        return new PurchaseResponse(
                "Purchase successful",
                invoice
        );
    }

    private boolean processPayment(PaymentInfo paymentInfo, BigDecimal total) {
        return paymentInfo.getCardNumber() != null && paymentInfo.getCardNumber().length() >= 12;
    }
}
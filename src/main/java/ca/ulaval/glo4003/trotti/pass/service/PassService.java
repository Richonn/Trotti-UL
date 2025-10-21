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
import java.time.Year;
import java.util.stream.IntStream;

public class PassService {
    private static final BigDecimal BASE_PRICE = BigDecimal.valueOf(45);
    private static final BigDecimal EXTRA_PRICE_PER_BLOCK = BigDecimal.valueOf(2);

    public PurchaseResponse purchase(PurchaseRequest request, User user) {
        BigDecimal totalPrice = getTotalPrice(request);
        setPaymentInfo(request, user, totalPrice);

        Pass pass = getUserPass(request, user, totalPrice);

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

    private static Pass getUserPass(PurchaseRequest request, User user, BigDecimal totalPrice) {
        Pass pass = new Pass(
                request.getSession(),
                request.getExtraMinutesPerDay(),
                request.isMonthlyBilling(),
                totalPrice
        );
        user.addPass(pass);
        return pass;
    }

    private static BigDecimal getTotalPrice(PurchaseRequest request) {
        int months = SessionUtil.getSessionDurationInMonths(request.getSession());
        int extraBlocks = request.getExtraMinutesPerDay();

        if (extraBlocks < 0)
            throw new IllegalArgumentException("Extra minutes must be greater or equal to 0");
        if (extraBlocks % 10 != 0)
            throw new IllegalArgumentException("Extra minutes must be divisible by 10.");

        extraBlocks /= 10;

        BigDecimal extraPrice = EXTRA_PRICE_PER_BLOCK
                .multiply(BigDecimal.valueOf(extraBlocks))
                .multiply(BigDecimal.valueOf(months));

        return BASE_PRICE.add(extraPrice);
    }

    private void setPaymentInfo(PurchaseRequest request, User user, BigDecimal totalPrice) {
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

        if (!processPayment(paymentInfo)) {
            throw new RuntimeException("Payment failed");
        }
    }

    private boolean verifyExpiryDate(String date) {
        if (!date.matches("\\d{2}/\\d{2}"))
            return false;

        String[] parts = date.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);
        if (month < 1 || month > 12) {
            return false;
        }

        int currentYear = Year.now().getValue() % 100;
        int currentMonth = LocalDate.now().getMonthValue();
        return !(year < currentYear || (year == currentYear && month < currentMonth));
    }

    private boolean verifyCardNumber(String cardNumber) {
        if (cardNumber == null ||
            cardNumber.length() < 8 ||
            cardNumber.length() > 16) {
            return false;
        }

        int[] numbers = new int[cardNumber.length()];
        for (int i = 0; i < cardNumber.length(); i++) {
            numbers[i] = Character.getNumericValue(cardNumber.charAt(i));
            if ((i + 1) % 2 == 1) {
                numbers[i] = (numbers[i] * 2) % 10 + ((numbers[i] * 2) / 10);
            }
        }

        return IntStream.of(numbers).sum() % 10 == 0;
    }

    private boolean verifyCvv(String cvv, String cardNumber) {
        if (cvv == null || !cvv.matches("\\d{3,4}")) {
            return false;
        }

        if (cardNumber != null && (cardNumber.startsWith("34") || cardNumber.startsWith("37"))) {
            return cvv.length() == 4;
        } else {
            return cvv.length() == 3;
        }
    }

    private boolean processPayment(PaymentInfo paymentInfo) {
        return verifyCardNumber(paymentInfo.getCardNumber())
            && verifyExpiryDate(paymentInfo.getExpiryDate())
            && verifyCvv(paymentInfo.getCvv(), paymentInfo.getCardNumber());
    }

    public boolean hasActiveSession(User user) {
        LocalDate today = LocalDate.now();

        for (Pass pass : user.getPurchasedPasses()) {
            LocalDate startDate = SessionUtil.getSessionStartDate(pass.getSession());
            LocalDate endDate = SessionUtil.getSessionEndDate(pass.getSession());

            if (!today.isBefore(startDate) && !today.isAfter(endDate)) {
                return true;
            }
        }
        return false;
    }
}

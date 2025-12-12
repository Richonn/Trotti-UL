package ca.ulaval.glo4003.trotti.interfaces.rest.user.response;

public class PaymentResponse {
    private final String message;
    private final InvoiceResponse invoice;

    public PaymentResponse(String message, InvoiceResponse invoice) {
        this.message = message;
        this.invoice = invoice;
    }

    public String getMessage() {
        return message;
    }

    public InvoiceResponse getInvoice() {
        return invoice;
    }
}

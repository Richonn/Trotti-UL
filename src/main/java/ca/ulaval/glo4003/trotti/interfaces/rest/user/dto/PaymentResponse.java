package ca.ulaval.glo4003.trotti.interfaces.rest.user.dto;

public class PaymentResponse {
    private final String message;
    private final InvoiceDto invoice;

    public PaymentResponse(String message, InvoiceDto invoice) {
        this.message = message;
        this.invoice = invoice;
    }

    public String getMessage() {
        return message;
    }

    public InvoiceDto getInvoice() {
        return invoice;
    }
}

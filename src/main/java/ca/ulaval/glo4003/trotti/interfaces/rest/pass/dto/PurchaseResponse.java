package ca.ulaval.glo4003.trotti.interfaces.rest.pass.dto;

public class PurchaseResponse {
    private final String message;
    private final InvoiceDto invoice;

    public PurchaseResponse(String message, InvoiceDto invoice) {
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

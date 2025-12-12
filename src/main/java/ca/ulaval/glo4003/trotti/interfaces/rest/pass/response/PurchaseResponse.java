package ca.ulaval.glo4003.trotti.interfaces.rest.pass.response;

public class PurchaseResponse {
    private final String message;
    private final InvoiceResponse invoice;

    public PurchaseResponse(String message, InvoiceResponse invoice) {
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

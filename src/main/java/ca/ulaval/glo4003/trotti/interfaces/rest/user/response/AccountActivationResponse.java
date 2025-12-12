package ca.ulaval.glo4003.trotti.interfaces.rest.user.response;

public class AccountActivationResponse {
    private final String message;

    public AccountActivationResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

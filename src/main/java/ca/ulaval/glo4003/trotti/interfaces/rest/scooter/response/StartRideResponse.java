package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.response;

public class StartRideResponse {
    private String message;

    public StartRideResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

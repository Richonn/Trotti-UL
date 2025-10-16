package ca.ulaval.glo4003.trotti.scooter.dto;

public class StartRideResponse {
    private String message;

    public StartRideResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

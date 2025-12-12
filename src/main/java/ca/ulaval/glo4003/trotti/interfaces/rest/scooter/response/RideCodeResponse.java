package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.response;

import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.response.RideCodeModel;

public class RideCodeResponse {
    private final String message;
    private final String code;
    private final String expiresAt;

    public RideCodeResponse(RideCodeModel rideCode) {
        this.message = "Code sent by email";
        this.code = rideCode.getCode();
        this.expiresAt = rideCode.getExpirationTime().toString();
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public String getExpiresAt() {
        return expiresAt;
    }
}

package ca.ulaval.glo4003.trotti.scooter.dto;

import ca.ulaval.glo4003.trotti.scooter.model.RideCode;

public class RideCodeResponse {
    private final String message;
    private final String code;
    private final String expiresAt;

    public RideCodeResponse(RideCode rideCode) {
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

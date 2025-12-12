package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.response;

import java.time.LocalDateTime;

public class RideCodeModel {
    private final String code;
    private final LocalDateTime expirationTime;

    public RideCodeModel(String code, LocalDateTime expirationTime) {
        this.code = code;
        this.expirationTime = expirationTime;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationTime);
    }
}
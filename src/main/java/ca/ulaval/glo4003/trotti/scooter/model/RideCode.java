package ca.ulaval.glo4003.trotti.scooter.model;

import java.time.LocalDateTime;

public class RideCode {
    private final String code;
    private final LocalDateTime expirationTime;

    public RideCode(String code, LocalDateTime expirationTime) {
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
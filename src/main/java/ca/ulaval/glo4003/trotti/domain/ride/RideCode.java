package ca.ulaval.glo4003.trotti.domain.ride;

import java.time.LocalDateTime;
import java.util.Objects;

public class RideCode {
    private final String code;
    private final LocalDateTime expirationTime;

    public RideCode(String code, LocalDateTime expirationTime) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Ride code cannot be empty");
        }
        this.code = code;
        this.expirationTime = Objects.requireNonNull(expirationTime, "Expiration time cannot be null");
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

    public boolean matches(String inputCode) {
        return this.code.equals(inputCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RideCode rideCode = (RideCode) o;
        return Objects.equals(code, rideCode.code) &&
                Objects.equals(expirationTime, rideCode.expirationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, expirationTime);
    }

    @Override
    public String toString() {
        return code;
    }
}

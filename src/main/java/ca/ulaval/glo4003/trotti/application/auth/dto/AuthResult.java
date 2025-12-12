package ca.ulaval.glo4003.trotti.application.auth.dto;

public class AuthResult {
    private final String userEmail;
    private final boolean isActivated;
    private final String token;

    public AuthResult(String userEmail, boolean isActivated, String token) {
        this.userEmail = userEmail;
        this.isActivated = isActivated;
        this.token = token;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public String getToken() {
        return token;
    }
}

package ca.ulaval.glo4003.trotti.interfaces.rest.security;

import ca.ulaval.glo4003.trotti.domain.security.TokenGenerator;

public class AuthenticationService {
    private final TokenGenerator tokenGenerator;

    public AuthenticationService(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    public String extractEmailFromToken(String token) {
        return tokenGenerator.extractEmail(token);
    }
}

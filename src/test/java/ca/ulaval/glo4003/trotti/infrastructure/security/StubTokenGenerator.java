package ca.ulaval.glo4003.trotti.infrastructure.security;

import ca.ulaval.glo4003.trotti.domain.security.TokenGenerator;

public class StubTokenGenerator implements TokenGenerator {
    @Override
    public String generateToken(String email) {
        return "stub-token-" + email;
    }

    @Override
    public String extractEmail(String token) {
        if (token.startsWith("stub-token-")) {
            return token.substring("stub-token-".length());
        }
        return null;
    }
}

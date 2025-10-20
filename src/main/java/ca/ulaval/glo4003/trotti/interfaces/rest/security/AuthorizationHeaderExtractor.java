package ca.ulaval.glo4003.trotti.interfaces.rest.security;

public class AuthorizationHeaderExtractor {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = 7;

    public static String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            throw new IllegalArgumentException("Authorization header is required");
        }

        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("Authorization header must start with 'Bearer '");
        }

        if (authorizationHeader.length() <= BEARER_PREFIX_LENGTH) {
            throw new IllegalArgumentException("Authorization token is missing");
        }

        return authorizationHeader.substring(BEARER_PREFIX_LENGTH);
    }
}

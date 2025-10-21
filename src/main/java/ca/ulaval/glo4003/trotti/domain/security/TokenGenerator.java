package ca.ulaval.glo4003.trotti.domain.security;

public interface TokenGenerator {
    String generateToken(String email);
    String extractEmail(String token);
}

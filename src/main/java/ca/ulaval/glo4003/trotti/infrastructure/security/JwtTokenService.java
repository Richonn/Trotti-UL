package ca.ulaval.glo4003.trotti.infrastructure.security;

import ca.ulaval.glo4003.trotti.domain.security.TokenGenerator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtTokenService implements TokenGenerator {
    private final Key secretKey;
    private final long expirationTime;

    public JwtTokenService() {
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        this.expirationTime = 60 * 60 * 1000;
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}

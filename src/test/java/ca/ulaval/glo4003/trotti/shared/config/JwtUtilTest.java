package ca.ulaval.glo4003.trotti.shared.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void generateToken_thenExtractEmail_shouldReturnSameEmail() {
        String email = "test@email.com";
        String token = JwtUtil.generateToken(email);

        String extracted = JwtUtil.extractEmail(token);

        assertEquals(email, extracted);
    }
}

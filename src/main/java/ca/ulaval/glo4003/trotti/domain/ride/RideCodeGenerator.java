package ca.ulaval.glo4003.trotti.domain.ride;

import java.time.LocalDateTime;
import java.util.Random;

public class RideCodeGenerator {
    private static final int MIN_CODE_LENGTH = 4;
    private static final int MAX_CODE_LENGTH = 6;
    private static final int CODE_VALIDITY_SECONDS = 60;

    private final Random random = new Random();

    public RideCode generate() {
        String code = generateNumericCode(MIN_CODE_LENGTH, MAX_CODE_LENGTH);
        LocalDateTime expirationTime = LocalDateTime.now().plusSeconds(CODE_VALIDITY_SECONDS);
        return new RideCode(code, expirationTime);
    }

    private String generateNumericCode(int minLength, int maxLength) {
        int length = random.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}

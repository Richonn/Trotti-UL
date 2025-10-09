package ca.ulaval.glo4003.trotti.scooter.service;

import ca.ulaval.glo4003.trotti.scooter.model.RideCode;

import java.time.LocalDateTime;
import java.util.Random;

public class RideService {

    public RideCode generateCode() {
        String code = generateNumericCode(4, 6);
        LocalDateTime expirationTime = LocalDateTime.now().plusSeconds(60);
        return new RideCode(code, expirationTime);
    }

    private String generateNumericCode(int minLength, int maxLength) {
        int length = new Random().nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(new Random().nextInt(10));
        }
        return code.toString();
    }

    public boolean isCodeValid(RideCode rideCode, String inputCode) {
        return rideCode.getCode().equals(inputCode) && !rideCode.isExpired();
    }
}

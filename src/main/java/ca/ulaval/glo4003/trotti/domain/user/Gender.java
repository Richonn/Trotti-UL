package ca.ulaval.glo4003.trotti.domain.user;

public enum Gender {
    HOMME("H"),
    FEMME("F"),
    AUTRE("A"),
    PREFER_NOT_TO_SAY("N");

    private final String code;

    Gender(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Gender fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Gender cannot be empty");
        }

        for (Gender gender : Gender.values()) {
            if (gender.code.equalsIgnoreCase(value) || gender.name().equalsIgnoreCase(value)) {
                return gender;
            }
        }

        throw new IllegalArgumentException("Invalid gender: " + value);
    }
}

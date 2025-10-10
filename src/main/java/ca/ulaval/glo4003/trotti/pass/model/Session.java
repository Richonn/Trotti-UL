package ca.ulaval.glo4003.trotti.pass.model;

public enum Session {
    A2025("Automne 2025"),
    H2026("Hiver 2026"),
    E2026("Été 2026");

    private final String description;

    Session(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Session getInstance(String value) {
        for (Session session : Session.values()) {
            if (session.name().equalsIgnoreCase(value)) {
                return session;
            }
        }
        throw new IllegalArgumentException("Session inconnue: " + value);
    }
}

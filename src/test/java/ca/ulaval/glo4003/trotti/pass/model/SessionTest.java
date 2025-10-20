package ca.ulaval.glo4003.trotti.pass.model;

import ca.ulaval.glo4003.trotti.domain.pass.Session;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    @Test
    void getDescription_shouldReturnCorrectDescription_forA2025() {
        assertEquals("Automne 2025", Session.A2025.getDescription());
    }

    @Test
    void getDescription_shouldReturnCorrectDescription_forH2026() {
        assertEquals("Hiver 2026", Session.H2026.getDescription());
    }

    @Test
    void getDescription_shouldReturnCorrectDescription_forE2026() {
        assertEquals("Été 2026", Session.E2026.getDescription());
    }

    @Test
    void getInstance_shouldReturnA2025_whenValueIsA2025() {
        Session session = Session.getInstance("A2025");
        assertEquals(Session.A2025, session);
    }

    @Test
    void getInstance_shouldReturnH2026_whenValueIsH2026() {
        Session session = Session.getInstance("H2026");
        assertEquals(Session.H2026, session);
    }

    @Test
    void getInstance_shouldReturnE2026_whenValueIsE2026() {
        Session session = Session.getInstance("E2026");
        assertEquals(Session.E2026, session);
    }

    @Test
    void getInstance_shouldBeCaseInsensitive() {
        assertEquals(Session.A2025, Session.getInstance("a2025"));
        assertEquals(Session.A2025, Session.getInstance("A2025"));
        assertEquals(Session.H2026, Session.getInstance("h2026"));
        assertEquals(Session.E2026, Session.getInstance("e2026"));
    }

    @Test
    void getInstance_shouldThrowException_whenValueIsUnknown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Session.getInstance("INVALID")
        );

        assertTrue(exception.getMessage().contains("Session inconnue"));
        assertTrue(exception.getMessage().contains("INVALID"));
    }

    @Test
    void getInstance_shouldThrowException_whenValueIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Session.getInstance(null)
        );

        assertTrue(exception.getMessage().contains("Session inconnue"));
    }

    @Test
    void getInstance_shouldThrowException_whenValueIsEmpty() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Session.getInstance("")
        );

        assertTrue(exception.getMessage().contains("Session inconnue"));
    }

    @Test
    void values_shouldReturnAllSessions() {
        Session[] sessions = Session.values();

        assertEquals(3, sessions.length);
        assertEquals(Session.A2025, sessions[0]);
        assertEquals(Session.H2026, sessions[1]);
        assertEquals(Session.E2026, sessions[2]);
    }

    @Test
    void valueOf_shouldReturnCorrectSession() {
        assertEquals(Session.A2025, Session.valueOf("A2025"));
        assertEquals(Session.H2026, Session.valueOf("H2026"));
        assertEquals(Session.E2026, Session.valueOf("E2026"));
    }

    @Test
    void valueOf_shouldThrowException_whenValueIsInvalid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Session.valueOf("INVALID")
        );
    }

    @Test
    void name_shouldReturnCorrectName() {
        assertEquals("A2025", Session.A2025.name());
        assertEquals("H2026", Session.H2026.name());
        assertEquals("E2026", Session.E2026.name());
    }
}
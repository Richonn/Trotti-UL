package ca.ulaval.glo4003.trotti.pass;

import ca.ulaval.glo4003.trotti.pass.model.Session;
import ca.ulaval.glo4003.trotti.pass.util.SessionUtil;
import org.eclipse.jetty.http.HttpException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class SessionUtilTest {
    @Test
    void getSessionStartDate_ShouldReturnCorrectDate_ForA2025() {
        assertEquals(LocalDate.of(2025, 9, 1), SessionUtil.getSessionStartDate(Session.A2025));
    }

    @Test
    void getSessionStartDate_ShouldReturnCorrectDate_ForH2026() {
        assertEquals(LocalDate.of(2026, 1, 1), SessionUtil.getSessionStartDate(Session.H2026));
    }

    @Test
    void getSessionStartDate_ShouldReturnCorrectDate_ForAE2026() {
        assertEquals(LocalDate.of(2026, 5, 1), SessionUtil.getSessionStartDate(Session.E2026));
    }

    @Test
    void getSessionStartDate_ShouldThrowForUnknowSessionPrefix() {
        assertThrows(IllegalArgumentException.class, () ->
                SessionUtil.getSessionStartDate(Session.valueOf("X2025")));
    }
}

package ca.ulaval.glo4003.trotti.domain.pass;

import ca.ulaval.glo4003.trotti.domain.shared.Clock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SessionRepository {
    private final Clock clock;

    public SessionRepository(Clock clock) {
        this.clock = clock;
    }

    public List<Session> getAvailableSessions() {
        LocalDate today = clock.today();
        int currentYear = today.getYear();

        List<Session> sessions = new ArrayList<>();

        for (int year = currentYear; year <= currentYear + 1; year++) {
            sessions.add(Session.generate(year, "Hiver"));
            sessions.add(Session.generate(year, "Été"));
            sessions.add(Session.generate(year, "Automne"));
        }

        return sessions.stream()
            .filter(s -> s.isActive(today))
            .collect(Collectors.toList());
    }

    public Session getSessionById(String id) {
        return Session.fromId(id);
    }
}

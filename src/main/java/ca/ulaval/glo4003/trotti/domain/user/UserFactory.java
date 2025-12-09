package ca.ulaval.glo4003.trotti.domain.user;

import java.time.LocalDate;
import java.util.UUID;

public class UserFactory {
    public User createForRegistration(Email email, Password password, String name,
                                     String dateOfBirth, String gender, String idul, Role role) {
        String userId = UUID.randomUUID().toString();
        return User.create(
                userId,
                email,
                password,
                name,
                LocalDate.parse(dateOfBirth),
                Gender.fromString(gender),
                new Idul(idul),
                role
        );
    }

    public User createWithId(String userId, Email email, Password password, String name,
                            LocalDate dateOfBirth, Gender gender, Idul idul, Role role) {
        return User.create(userId, email, password, name, dateOfBirth, gender, idul, role);
    }
}

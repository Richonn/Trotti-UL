package ca.ulaval.glo4003.trotti.interfaces.rest.security;

import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.trotti.domain.user.exception.InvalidRoleException;
import ca.ulaval.glo4003.trotti.interfaces.rest.config.ApplicationContext;

public class RoleChecker {

    public static void check(String email, Role role) {
        ApplicationContext context = ApplicationContext.getInstance();
        UserRepository userRepository = context.getUserRepository();

        User user = userRepository.findByEmail(new Email(email))
                .orElseThrow(InvalidCredentialsException::new);

        if (user.getRole() != role)
            throw new InvalidRoleException();
    }

}

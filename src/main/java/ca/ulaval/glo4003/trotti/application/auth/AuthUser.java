package ca.ulaval.glo4003.trotti.application.auth;

import ca.ulaval.glo4003.trotti.domain.user.Role;

import java.security.Principal;

public class AuthUser implements Principal {
    private final String email;
    private final boolean isActivated;
    private final Role role;

    public AuthUser(String email, boolean isActivated, Role role) {
        this.email = email;
        this.isActivated = isActivated;
        this.role = role;
    }

    @Override
    public String getName() {
        return email;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public Role getRole() {
        return role;
    }
}

package ca.ulaval.glo4003.trotti.interfaces.rest.security;

import ca.ulaval.glo4003.trotti.application.auth.AuthUser;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.interfaces.rest.config.ApplicationContext;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import java.security.Principal;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public AuthenticationFilter() {
        ApplicationContext context = ApplicationContext.getInstance();
        this.authenticationService = context.getAuthenticationService();
        this.userRepository = context.getUserRepository();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();

        if (isPublicEndpoint(path)) {
            return;
        }

        String authHeader = requestContext.getHeaderString("Authorization");

        if (authHeader == null || authHeader.trim().isEmpty()) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Missing Authorization header\"}")
                    .build()
            );
            return;
        }

        AuthUser authUser = null;

        if (authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authUser = validateBearerToken(token);
        }

        if (authUser == null) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Invalid or expired token\"}")
                    .build()
            );
            return;
        }

        final AuthUser finalAuthUser = authUser;
        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return finalAuthUser;
            }

            @Override
            public boolean isUserInRole(String role) {
                return finalAuthUser.getRole().name().equals(role);
            }

            @Override
            public boolean isSecure() {
                return requestContext.getUriInfo().getRequestUri().getScheme().equals("https");
            }

            @Override
            public String getAuthenticationScheme() {
                return "Bearer";
            }
        });
    }

    private boolean isPublicEndpoint(String path) {
        return path.equals("user/register") ||
               path.equals("user/login");
    }

    private AuthUser validateBearerToken(String token) {
        try {
            String email = authenticationService.extractEmailFromToken(token);
            User user = userRepository.findByEmail(new Email(email)).orElse(null);

            if (user == null) {
                return null;
            }

            return new AuthUser(user.getEmail().getValue(), user.isActivated(), user.getRole());
        } catch (Exception e) {
            return null;
        }
    }
}

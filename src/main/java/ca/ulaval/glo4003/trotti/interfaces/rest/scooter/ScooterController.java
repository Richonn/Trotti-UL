package ca.ulaval.glo4003.trotti.interfaces.rest.scooter;

import ca.ulaval.glo4003.trotti.application.scooter.TransferScooterUseCase;
import ca.ulaval.glo4003.trotti.domain.security.TokenGenerator;
import ca.ulaval.glo4003.trotti.domain.shared.DomainException;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.interfaces.rest.config.ApplicationContext;
import ca.ulaval.glo4003.trotti.interfaces.rest.error.ErrorResponseBuilder;
import ca.ulaval.glo4003.trotti.interfaces.rest.security.AuthorizationHeaderExtractor;
import ca.ulaval.glo4003.trotti.interfaces.rest.security.RoleChecker;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/scooters")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ScooterController {

    private final TransferScooterUseCase transferScooterUseCase;
    private final TokenGenerator tokenGenerator;

    public ScooterController() {
        ApplicationContext context = ApplicationContext.getInstance();
        this.transferScooterUseCase = context.getTransferScooterUseCase();
        this.tokenGenerator = context.getTokenGenerator();
    }

    @Path("/{scooterId}/transfer")
    public Response transferScooter(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("scooterId") String scooterId) {
        try {
            String token = AuthorizationHeaderExtractor.extractToken(authHeader);
            String email = tokenGenerator.extractEmail(token);
            RoleChecker.check(email, Role.TECHNICIAN);

            // Implement Use Case

            return Response.ok().build();
        } catch (DomainException e) {
            return ErrorResponseBuilder.buildErrorResponse(e);
        } catch (Exception e) {
            return ErrorResponseBuilder.buildInternalServerError(e);
        }
    }

}

package ca.ulaval.glo4003.trotti.interfaces.rest.scooter;

import ca.ulaval.glo4003.trotti.application.scooter.EndTransferScooterUseCase;
import ca.ulaval.glo4003.trotti.application.scooter.StartTransferScooterUseCase;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.interfaces.rest.config.ApplicationContext;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto.EndTransferRequest;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto.StartTransferRequest;
import ca.ulaval.glo4003.trotti.interfaces.rest.security.AuthenticationService;
import ca.ulaval.glo4003.trotti.interfaces.rest.security.AuthorizationHeaderExtractor;
import ca.ulaval.glo4003.trotti.interfaces.rest.security.RoleChecker;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/scooters")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ScooterController {

    private final StartTransferScooterUseCase startTransferScooterUseCase;
    private final EndTransferScooterUseCase endTransferScooterUseCase;
    private final AuthenticationService authenticationService;

    public ScooterController() {
        ApplicationContext context = ApplicationContext.getInstance();
        this.startTransferScooterUseCase = context.getStartTransferScooterUseCase();
        this.endTransferScooterUseCase = context.getEndTransferScooterUseCase();
        this.authenticationService = context.getAuthenticationService();
    }

    @Path("/transfer/start")
    @POST
    public Response startTransferScooter(
            @HeaderParam("Authorization") String authHeader,
            StartTransferRequest request) {
        String token = AuthorizationHeaderExtractor.extractToken(authHeader);
        String email = authenticationService.extractEmailFromToken(token);
        RoleChecker.check(email, Role.TECHNICIAN);

        String scooterId = startTransferScooterUseCase.execute(Integer.parseInt(request.getSlot()), request.getStation());

        return Response.ok(scooterId).build();
    }

    @Path("/transfer/end")
    @POST
    public Response endTransferScooter(
            @HeaderParam("Authorization") String authHeader,
            EndTransferRequest request) {
        String token = AuthorizationHeaderExtractor.extractToken(authHeader);
        String email = authenticationService.extractEmailFromToken(token);
        RoleChecker.check(email, Role.TECHNICIAN);

        endTransferScooterUseCase.execute(request.getScooterId(), Integer.parseInt(request.getSlot()), request.getStation());

        return Response.ok().build();
    }
}

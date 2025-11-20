package ca.ulaval.glo4003.trotti.interfaces.rest.station;

import ca.ulaval.glo4003.trotti.application.station.EndStationMaintenanceUseCase;
import ca.ulaval.glo4003.trotti.application.station.RequestStationMaintenanceUseCase;
import ca.ulaval.glo4003.trotti.application.station.StartStationMaintenanceUseCase;
import ca.ulaval.glo4003.trotti.domain.security.TokenGenerator;
import ca.ulaval.glo4003.trotti.domain.shared.DomainException;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.interfaces.rest.config.ApplicationContext;
import ca.ulaval.glo4003.trotti.interfaces.rest.error.ErrorResponseBuilder;
import ca.ulaval.glo4003.trotti.interfaces.rest.security.AuthorizationHeaderExtractor;
import ca.ulaval.glo4003.trotti.interfaces.rest.security.RoleChecker;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/stations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StationController {

    private final StartStationMaintenanceUseCase startStationMaintenanceUseCase;
    private final EndStationMaintenanceUseCase endStationMaintenanceUseCase;
    private final RequestStationMaintenanceUseCase requestStationMaintenanceUseCase;
    private final TokenGenerator tokenGenerator;

    public StationController() {
        ApplicationContext context = ApplicationContext.getInstance();
        this.startStationMaintenanceUseCase = context.getStartStationMaintenanceUseCase();
        this.endStationMaintenanceUseCase = context.getEndStationMaintenanceUseCase();
        this.requestStationMaintenanceUseCase = context.getRequestStationMaintenanceUseCase();
        this.tokenGenerator = context.getTokenGenerator();
    }

    @POST
    @Path("/{stationId}/maintenance/start")
    public Response startMaintenance(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("stationId") String stationId) {
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

    @POST
    @Path("/{stationId}/maintenance/end")
    public Response endMaintenance(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("stationId") String stationId) {
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

    @POST
    @Path("/{stationId}/maintenance")
    public Response requestMaintenance(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("stationId") String stationId) {
        try {
            String token = AuthorizationHeaderExtractor.extractToken(authHeader);
            String email = tokenGenerator.extractEmail(token);

            this.requestStationMaintenanceUseCase.execute(stationId, email);

            return Response.ok().build();
        } catch (DomainException e) {
            return ErrorResponseBuilder.buildErrorResponse(e);
        } catch (Exception e) {
            return ErrorResponseBuilder.buildInternalServerError(e);
        }
    }

}

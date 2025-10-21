package ca.ulaval.glo4003.trotti.interfaces.rest.ride;

import ca.ulaval.glo4003.trotti.application.ride.RequestRideCodeUseCase;
import ca.ulaval.glo4003.trotti.application.ride.RideStartResult;
import ca.ulaval.glo4003.trotti.application.ride.StartRideUseCase;
import ca.ulaval.glo4003.trotti.domain.ride.RideCode;
import ca.ulaval.glo4003.trotti.domain.security.TokenGenerator;
import ca.ulaval.glo4003.trotti.domain.shared.DomainException;
import ca.ulaval.glo4003.trotti.interfaces.rest.config.ApplicationContext;
import ca.ulaval.glo4003.trotti.interfaces.rest.error.ErrorResponseBuilder;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto.RideCodeResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto.StartRideRequest;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto.StartRideResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.security.AuthorizationHeaderExtractor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/ride")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RideController {
    private final RequestRideCodeUseCase requestRideCodeUseCase;
    private final StartRideUseCase startRideUseCase;
    private final TokenGenerator tokenGenerator;

    public RideController() {
        ApplicationContext context = ApplicationContext.getInstance();
        this.requestRideCodeUseCase = context.getRequestRideCodeUseCase();
        this.startRideUseCase = context.getStartRideUseCase();
        this.tokenGenerator = context.getTokenGenerator();
    }

    @POST
    @Path("/request-code")
    public Response requestRideCode(@HeaderParam("Authorization") String authHeader) {
        try {
            String token = AuthorizationHeaderExtractor.extractToken(authHeader);
            String email = tokenGenerator.extractEmail(token);

            RideCode rideCode = requestRideCodeUseCase.execute(email);

            ca.ulaval.glo4003.trotti.interfaces.rest.scooter.model.RideCode dtoRideCode =
                    new ca.ulaval.glo4003.trotti.interfaces.rest.scooter.model.RideCode(rideCode.getCode(), rideCode.getExpirationTime());
            return Response.ok(new RideCodeResponse(dtoRideCode)).build();
        } catch (DomainException e) {
            Response.Status status = determineStatusForException(e);
            return ErrorResponseBuilder.buildErrorResponse(e, status);
        } catch (Exception e) {
            return ErrorResponseBuilder.buildInternalServerError(e);
        }
    }

    @POST
    @Path("/start")
    public Response startRide(@HeaderParam("Authorization") String authHeader, StartRideRequest request) {
        try {
            String token = AuthorizationHeaderExtractor.extractToken(authHeader);
            String email = tokenGenerator.extractEmail(token);

            RideStartResult result = startRideUseCase.execute(
                    email,
                    request.getStation(),
                    Integer.parseInt(request.getSlot()),
                    request.getCode()
            );

            String message = "Ride started successfully with scooter having battery level: " +
                           result.getBatteryLevel() + "%";
            return Response.ok(new StartRideResponse(message)).build();
        } catch (DomainException e) {
            Response.Status status = determineStatusForStartRideException(e);
            return ErrorResponseBuilder.buildErrorResponse(e, status);
        } catch (Exception e) {
            return ErrorResponseBuilder.buildInternalServerError(e);
        }
    }

    private Response.Status determineStatusForException(DomainException e) {
        String simpleName = e.getClass().getSimpleName();
        if (simpleName.contains("NotActivated")) {
            return Response.Status.FORBIDDEN;
        }
        if (simpleName.contains("NotFound")) {
            return Response.Status.UNAUTHORIZED;
        }
        return Response.Status.FORBIDDEN;
    }

    private Response.Status determineStatusForStartRideException(DomainException e) {
        String simpleName = e.getClass().getSimpleName();
        if (simpleName.contains("NotFound")) {
            return Response.Status.NOT_FOUND;
        }
        if (simpleName.contains("NotActivated") || simpleName.contains("Code") || simpleName.contains("NoActive")) {
            return Response.Status.FORBIDDEN;
        }
        return Response.Status.BAD_REQUEST;
    }
}

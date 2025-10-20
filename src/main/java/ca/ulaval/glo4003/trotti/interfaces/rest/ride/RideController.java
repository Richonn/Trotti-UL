package ca.ulaval.glo4003.trotti.interfaces.rest.ride;

import ca.ulaval.glo4003.trotti.application.ride.RequestRideCodeUseCase;
import ca.ulaval.glo4003.trotti.application.ride.RideStartResult;
import ca.ulaval.glo4003.trotti.application.ride.StartRideUseCase;
import ca.ulaval.glo4003.trotti.domain.ride.RideCode;
import ca.ulaval.glo4003.trotti.domain.shared.DomainException;
import ca.ulaval.glo4003.trotti.interfaces.rest.config.ApplicationContext;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto.RideCodeResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto.StartRideRequest;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto.StartRideResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/ride")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RideController {
    private final RequestRideCodeUseCase requestRideCodeUseCase;
    private final StartRideUseCase startRideUseCase;

    public RideController() {
        ApplicationContext context = ApplicationContext.getInstance();
        this.requestRideCodeUseCase = context.getRequestRideCodeUseCase();
        this.startRideUseCase = context.getStartRideUseCase();
    }

    @POST
    @Path("/request-code")
    public Response requestRideCode(@HeaderParam("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = ApplicationContext.getInstance().getJwtTokenService().extractEmail(token);

            RideCode rideCode = requestRideCodeUseCase.execute(email);

            ca.ulaval.glo4003.trotti.interfaces.rest.scooter.model.RideCode dtoRideCode =
                    new ca.ulaval.glo4003.trotti.interfaces.rest.scooter.model.RideCode(rideCode.getCode(), rideCode.getExpirationTime());
            return Response.ok(new RideCodeResponse(dtoRideCode)).build();
        } catch (DomainException e) {
            int status = e.getClass().getSimpleName().contains("NotActivated") ? 403 :
                        e.getClass().getSimpleName().contains("NotFound") ? 401 : 403;
            return Response.status(status)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/start")
    public Response startRide(@HeaderParam("Authorization") String authHeader, StartRideRequest request) {
        try {
            String token = authHeader.substring(7);
            String email = ApplicationContext.getInstance().getJwtTokenService().extractEmail(token);

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
            int status = e.getClass().getSimpleName().contains("NotFound") ? 404 :
                        e.getClass().getSimpleName().contains("NotActivated") ||
                        e.getClass().getSimpleName().contains("Code") ||
                        e.getClass().getSimpleName().contains("NoActive") ? 403 : 400;
            return Response.status(status)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}

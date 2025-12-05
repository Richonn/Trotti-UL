package ca.ulaval.glo4003.trotti.interfaces.rest.ride;

import ca.ulaval.glo4003.trotti.application.ride.EndRideUseCase;
import ca.ulaval.glo4003.trotti.application.ride.GetRideHistoryUseCase;
import ca.ulaval.glo4003.trotti.application.ride.GetTripHistoryWithSummaryUseCase;
import ca.ulaval.glo4003.trotti.application.ride.RequestRideCodeUseCase;
import ca.ulaval.glo4003.trotti.application.ride.RideEndResult;
import ca.ulaval.glo4003.trotti.application.ride.RideStartResult;
import ca.ulaval.glo4003.trotti.application.ride.StartRideUseCase;
import ca.ulaval.glo4003.trotti.application.ride.TripHistoryResult;
import ca.ulaval.glo4003.trotti.domain.ride.Ride;
import ca.ulaval.glo4003.trotti.domain.ride.RideCode;
import ca.ulaval.glo4003.trotti.domain.shared.DomainException;
import ca.ulaval.glo4003.trotti.interfaces.rest.config.ApplicationContext;
import ca.ulaval.glo4003.trotti.interfaces.rest.error.ErrorResponseBuilder;
import ca.ulaval.glo4003.trotti.interfaces.rest.security.AuthenticationService;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto.EndRideRequest;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto.EndRideResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto.RideCodeResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.ride.dto.TripHistoryResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto.RideHistoryResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto.StartRideRequest;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.dto.StartRideResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.ride.mapper.RideResponseMapper;
import ca.ulaval.glo4003.trotti.interfaces.rest.security.AuthorizationHeaderExtractor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/ride")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RideController {
    private final RequestRideCodeUseCase requestRideCodeUseCase;
    private final StartRideUseCase startRideUseCase;
    private final EndRideUseCase endRideUseCase;
    private final GetRideHistoryUseCase getRideHistoryUseCase;
    private final GetTripHistoryWithSummaryUseCase getTripHistoryWithSummaryUseCase;
    private final AuthenticationService authenticationService;

    public RideController() {
        ApplicationContext context = ApplicationContext.getInstance();
        this.requestRideCodeUseCase = context.getRequestRideCodeUseCase();
        this.startRideUseCase = context.getStartRideUseCase();
        this.endRideUseCase = context.getEndRideUseCase();
        this.getRideHistoryUseCase = context.getGetRideHistoryUseCase();
        this.getTripHistoryWithSummaryUseCase = context.getGetTripHistoryWithSummaryUseCase();
        this.authenticationService = context.getAuthenticationService();
    }

    @POST
    @Path("/request-code")
    public Response requestRideCode(@HeaderParam("Authorization") String authHeader) {
        try {
            String token = AuthorizationHeaderExtractor.extractToken(authHeader);
            String email = authenticationService.extractEmailFromToken(token);

            RideCode rideCode = requestRideCodeUseCase.execute(email);
            ca.ulaval.glo4003.trotti.interfaces.rest.scooter.model.RideCode dtoRideCode = RideResponseMapper.toRideCodeModel(rideCode);
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
            String email = authenticationService.extractEmailFromToken(token);

            RideStartResult result = startRideUseCase.execute(
                    email,
                    request.getStation(),
                    Integer.parseInt(request.getSlot()),
                    request.getCode()
            );

            StartRideResponse response = RideResponseMapper.toStartRideResponse(result);
            return Response.ok(response).build();
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

    @POST
    @Path("/end")
    public Response endRide(@HeaderParam("Authorization") String authHeader, EndRideRequest request) {
        try {
            String token = AuthorizationHeaderExtractor.extractToken(authHeader);
            String email = authenticationService.extractEmailFromToken(token);

            RideEndResult result = endRideUseCase.execute(
                    email,
                    request.getStation(),
                    Integer.parseInt(request.getSlot())
            );

            EndRideResponse response = RideResponseMapper.toEndRideResponse(result);
            return Response.ok(response).build();
        } catch (DomainException e) {
            Response.Status status = determineStatusForEndRideException(e);
            return ErrorResponseBuilder.buildErrorResponse(e, status);
        } catch (Exception e) {
            return ErrorResponseBuilder.buildInternalServerError(e);
        }
    }

    private Response.Status determineStatusForEndRideException(DomainException e) {
        String simpleName = e.getClass().getSimpleName();
        if (simpleName.contains("NotFound")) {
            return Response.Status.NOT_FOUND;
        }
        if (simpleName.contains("NoActiveRide")) {
            return Response.Status.BAD_REQUEST;
        }
        if (simpleName.contains("PaymentDeclined")) {
            return Response.Status.PAYMENT_REQUIRED;
        }
        return Response.Status.BAD_REQUEST;
    }

    @GET
    @Path("/history")
    public Response getRideHistory(@HeaderParam("Authorization") String authHeader) {
        try {
            String token = AuthorizationHeaderExtractor.extractToken(authHeader);
            String email = authenticationService.extractEmailFromToken(token);

            List<Ride> rides = getRideHistoryUseCase.execute(email);
            List<RideHistoryResponse.RideHistoryItem> rideItems = RideResponseMapper.toRideHistoryItems(rides);
            return Response.ok(new RideHistoryResponse(rideItems)).build();
        } catch (DomainException e) {
            Response.Status status = determineStatusForException(e);
            return ErrorResponseBuilder.buildErrorResponse(e, status);
        } catch (Exception e) {
            return ErrorResponseBuilder.buildInternalServerError(e);
        }
    }

    @GET
    @Path("/trip-history")
    public Response getTripHistory(@HeaderParam("Authorization") String authHeader) {
        try {
            String token = AuthorizationHeaderExtractor.extractToken(authHeader);
            String email = authenticationService.extractEmailFromToken(token);

            TripHistoryResult result = getTripHistoryWithSummaryUseCase.execute(email);
            TripHistoryResponse response = RideResponseMapper.toTripHistoryResponse(result);
            return Response.ok(response).build();
        } catch (DomainException e) {
            Response.Status status = determineStatusForException(e);
            return ErrorResponseBuilder.buildErrorResponse(e, status);
        } catch (Exception e) {
            return ErrorResponseBuilder.buildInternalServerError(e);
        }
    }
}

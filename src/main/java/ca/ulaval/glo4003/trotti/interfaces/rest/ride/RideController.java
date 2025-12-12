package ca.ulaval.glo4003.trotti.interfaces.rest.ride;

import ca.ulaval.glo4003.trotti.application.ride.EndRideUseCase;
import ca.ulaval.glo4003.trotti.application.ride.GetRideHistoryUseCase;
import ca.ulaval.glo4003.trotti.application.ride.GetTripHistoryWithSummaryUseCase;
import ca.ulaval.glo4003.trotti.application.ride.RequestRideCodeUseCase;
import ca.ulaval.glo4003.trotti.application.ride.dto.RideEndResult;
import ca.ulaval.glo4003.trotti.application.ride.dto.RideStartResult;
import ca.ulaval.glo4003.trotti.application.ride.StartRideUseCase;
import ca.ulaval.glo4003.trotti.application.ride.dto.TripHistoryResult;
import ca.ulaval.glo4003.trotti.application.ride.dto.RideDTO;
import ca.ulaval.glo4003.trotti.domain.ride.RideCode;
import ca.ulaval.glo4003.trotti.interfaces.rest.config.ApplicationContext;
import ca.ulaval.glo4003.trotti.interfaces.rest.security.AuthenticationService;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.request.EndRideRequest;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.response.EndRideResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.response.RideCodeResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.ride.response.TripHistoryResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.response.RideHistoryResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.request.StartRideRequest;
import ca.ulaval.glo4003.trotti.interfaces.rest.scooter.response.StartRideResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.ride.mapper.RideResponseMapper;
import ca.ulaval.glo4003.trotti.application.auth.AuthUser;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

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
    public Response requestRideCode(@Context SecurityContext securityContext) {
        AuthUser authUser = (AuthUser) securityContext.getUserPrincipal();

        RideCode rideCode = requestRideCodeUseCase.execute(authUser.getEmail());
        ca.ulaval.glo4003.trotti.interfaces.rest.scooter.response.RideCodeModel dtoRideCode = RideResponseMapper.toRideCodeModel(rideCode);
        return Response.ok(new RideCodeResponse(dtoRideCode)).build();
    }

    @POST
    @Path("/start")
    public Response startRide(@Context SecurityContext securityContext, StartRideRequest request) {
        AuthUser authUser = (AuthUser) securityContext.getUserPrincipal();

        RideStartResult result = startRideUseCase.execute(
                authUser.getEmail(),
                request.getStation(),
                Integer.parseInt(request.getSlot()),
                request.getCode()
        );

        StartRideResponse response = RideResponseMapper.toStartRideResponse(result);
        return Response.ok(response).build();
    }

    @POST
    @Path("/end")
    public Response endRide(@Context SecurityContext securityContext, EndRideRequest request) {
        AuthUser authUser = (AuthUser) securityContext.getUserPrincipal();

        RideEndResult result = endRideUseCase.execute(
                authUser.getEmail(),
                request.getStation(),
                Integer.parseInt(request.getSlot())
        );

        EndRideResponse response = RideResponseMapper.toEndRideResponse(result);
        return Response.ok(response).build();
    }

    @GET
    @Path("/history")
    public Response getRideHistory(@Context SecurityContext securityContext) {
        AuthUser authUser = (AuthUser) securityContext.getUserPrincipal();

        List<RideDTO> rides = getRideHistoryUseCase.execute(authUser.getEmail());
        List<RideHistoryResponse.RideHistoryItem> rideItems = RideResponseMapper.toRideHistoryItems(rides);
        return Response.ok(new RideHistoryResponse(rideItems)).build();
    }

    @GET
    @Path("/trip-history")
    public Response getTripHistory(@Context SecurityContext securityContext) {
        AuthUser authUser = (AuthUser) securityContext.getUserPrincipal();

        TripHistoryResult result = getTripHistoryWithSummaryUseCase.execute(authUser.getEmail());
        TripHistoryResponse response = RideResponseMapper.toTripHistoryResponse(result);
        return Response.ok(response).build();
    }
}

package ca.ulaval.glo4003.trotti.scooter.controller;

import ca.ulaval.glo4003.trotti.scooter.dto.RideCodeResponse;
import ca.ulaval.glo4003.trotti.scooter.model.RideCode;
import ca.ulaval.glo4003.trotti.scooter.service.RideService;
import jakarta.ws.rs.*;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/ride")
@Produces(MediaType.APPLICATION_JSON)
public class RideController {

    private final RideService rideService = new RideService();

    @POST
    @Path("/request-code")
    public Response requestRideCode() {
        RideCode rideCode = rideService.generateCode();
        // TODO: Envoyer par email ici
        RideCodeResponse response = new RideCodeResponse(rideCode);
        return Response.ok(response).build();
    }
}

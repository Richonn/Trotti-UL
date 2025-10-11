package ca.ulaval.glo4003.trotti.scooter.controller;

import ca.ulaval.glo4003.trotti.pass.service.PassService;
import ca.ulaval.glo4003.trotti.scooter.dto.RideCodeResponse;
import ca.ulaval.glo4003.trotti.scooter.model.RideCode;
import ca.ulaval.glo4003.trotti.scooter.service.RideService;
import ca.ulaval.glo4003.trotti.shared.config.JwtUtil;
import ca.ulaval.glo4003.trotti.user.model.User;
import ca.ulaval.glo4003.trotti.user.service.UserService;
import jakarta.ws.rs.*;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/ride")
@Produces(MediaType.APPLICATION_JSON)
public class RideController {

    private final RideService rideService = new RideService();
    private final UserService userService = new UserService();
    private final PassService passService = new PassService();

    @POST
    @Path("/request-code")
    public Response requestRideCode(@HeaderParam("Authorization") String authHeader) {
        String email = JwtUtil.extractEmail(authHeader.substring(7));
        User user = userService.findByEmail(email);

        if (user == null) {
            return Response.status(401).entity("{\"error\":\"User not found\"}").build();
        }

        if (!user.isActivated()) {
            return Response.status(403).entity("{\"error\":\"Account not activated\"}").build();
        }

        if (!passService.hasActiveSession(user)) {
            return Response.status(403).entity("{\"error\":\"No active subscription\"}").build();
        }

        RideCode rideCode = rideService.generateAndSendCode(email);

        RideCodeResponse response = new RideCodeResponse(rideCode);
        return Response.ok(response).build();
    }
}

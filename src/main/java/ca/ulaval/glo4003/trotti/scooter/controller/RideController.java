package ca.ulaval.glo4003.trotti.scooter.controller;

import ca.ulaval.glo4003.trotti.pass.service.PassService;
import ca.ulaval.glo4003.trotti.scooter.dto.RideCodeResponse;
import ca.ulaval.glo4003.trotti.scooter.dto.StartRideRequest;
import ca.ulaval.glo4003.trotti.scooter.dto.StartRideResponse;
import ca.ulaval.glo4003.trotti.scooter.model.RideCode;
import ca.ulaval.glo4003.trotti.scooter.model.Scooter;
import ca.ulaval.glo4003.trotti.scooter.service.RideService;
import ca.ulaval.glo4003.trotti.scooter.service.StationsService;
import ca.ulaval.glo4003.trotti.shared.config.JwtUtil;
import ca.ulaval.glo4003.trotti.user.model.User;
import ca.ulaval.glo4003.trotti.user.service.UserService;
import jakarta.ws.rs.*;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/ride")
@Produces(MediaType.APPLICATION_JSON)
public class RideController {

    private final RideService rideService;
    private final UserService userService;
    private final PassService passService;
    private final StationsService stationsService;

    public RideController() {
        this(new RideService(), new UserService(), new PassService(), new StationsService());
    }

    public RideController(RideService rideService, UserService userService, PassService passService, StationsService stationsService) {
        this.rideService = rideService;
        this.userService = userService;
        this.passService = passService;
        this.stationsService = stationsService;
    }

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

    @POST
    @Path("/start")
    public Response startRide(@HeaderParam("Authorization") String authHeader, StartRideRequest request) {
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

      String inputCode = request.getCode();
      if (!rideService.belongsToUser(email, inputCode)) {
        return Response.status(403).entity("{\"error\":\"Code is invalid\"}").build();
      }
      RideCode rideCode = rideService.getUserCode(email);
      if (!rideService.isCodeValid(rideCode, inputCode)) {
        return Response.status(403).entity("{\"error\":\"Code is expired\"}").build();
      }

      try {
          Scooter scooter = stationsService.takeScooterFromStation(request.getStation(), Integer.parseInt(request.getSlot()));
          return Response.ok(new StartRideResponse("Ride started successfully with scooter having battery level: " + scooter.getBatteryLevel() + "%")).build();
      } catch (Exception e) {
          return Response.status(404).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
      }
    }
}

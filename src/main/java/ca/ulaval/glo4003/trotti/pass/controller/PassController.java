package ca.ulaval.glo4003.trotti.pass.controller;

import ca.ulaval.glo4003.trotti.pass.dto.PurchaseRequest;
import ca.ulaval.glo4003.trotti.pass.dto.PurchaseResponse;
import ca.ulaval.glo4003.trotti.pass.service.PassService;
import ca.ulaval.glo4003.trotti.shared.config.JwtUtil;
import ca.ulaval.glo4003.trotti.user.model.User;
import ca.ulaval.glo4003.trotti.user.service.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/pass")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PassController {
    private final PassService passService;
    private final UserService userService;

    public PassController() {
        this.passService = new PassService();
        this.userService = new UserService();
    }

    public PassController(PassService passService, UserService userService) {
        this.passService = passService;
        this.userService = userService;
    }

    private static boolean isTokenValid(String authHeader) {
        return authHeader == null || !authHeader.startsWith("Bearer ");
    }

    @POST
    @Path("/purchase")
    public Response purchase(@HeaderParam("Authorization") String authHeader, PurchaseRequest request) {
        try {
            if (isTokenValid(authHeader)) return Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\":\"Invalid or missing token\"}")
                .build();

            String token = authHeader.substring(7);
            String email = JwtUtil.extractEmail(token);

            User user = userService.findByEmail(email);
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"User not found\"}")
                    .build();
            }

            PurchaseResponse response = passService.purchase(request, user);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        }
    }

    @GET
    @Path("/my-purchases")
    public Response getUserPurchases(@HeaderParam("Authorization") String authHeader) {
        try {
            if (isTokenValid(authHeader)) return Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\":\"Invalid or missing token\"}")
                .build();

            String token = authHeader.substring(7);
            String email = JwtUtil.extractEmail(token);

            User user = userService.findByEmail(email);
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"User not found\"}")
                    .build();
            }

            return Response.ok(user.getPurchasedPasses()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        }
    }
}

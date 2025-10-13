package ca.ulaval.glo4003.trotti.user.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ca.ulaval.glo4003.trotti.user.dto.LoginRequest;
import ca.ulaval.glo4003.trotti.user.dto.RegisterResponse;
import ca.ulaval.glo4003.trotti.user.model.User;
import ca.ulaval.glo4003.trotti.user.service.UserService;
import ca.ulaval.glo4003.trotti.shared.config.JwtUtil;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    private final UserService userService = new UserService();

    @POST
    @Path("/register")
    public Response register(User user) {
        try {
            userService.register(user);
            return Response.status(Response.Status.CREATED)
                    .entity(new RegisterResponse(user))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        try {
            if (request == null || request.getEmail() == null || request.getPassword() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Email and password required.\"}")
                        .build();
            }

            User user = userService.login(request.getEmail(), request.getPassword());
            String token = JwtUtil.generateToken(user.getEmail());

            return Response.ok("{\"token\": \"" + token + "\"}").build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/all")
    public Response getAllUsers() {
        try {
            return Response.ok(userService.getAllUsers()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Can't get all user\"}")
                    .build();
        }
    }

    @POST
    @Path("/activate")
    public Response activateAccount(@HeaderParam("Authorization") String authHeader) {
        String email = JwtUtil.extractEmail(authHeader.substring(7));
        userService.activateAccount(email);
        return Response.ok("{\"message\":\"Account activated\"}").build();
    }
}

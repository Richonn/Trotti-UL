package ca.ulaval.glo4003.trotti.interfaces.rest.user;

import ca.ulaval.glo4003.trotti.application.user.ActivateAccountUseCase;
import ca.ulaval.glo4003.trotti.application.user.GetAllUsersUseCase;
import ca.ulaval.glo4003.trotti.application.user.LoginUserUseCase;
import ca.ulaval.glo4003.trotti.application.user.RegisterUserUseCase;
import ca.ulaval.glo4003.trotti.domain.security.TokenGenerator;
import ca.ulaval.glo4003.trotti.domain.shared.DomainException;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.interfaces.rest.config.ApplicationContext;
import ca.ulaval.glo4003.trotti.interfaces.rest.error.ErrorResponseBuilder;
import ca.ulaval.glo4003.trotti.interfaces.rest.security.AuthorizationHeaderExtractor;
import ca.ulaval.glo4003.trotti.interfaces.rest.user.dto.AccountActivationResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.user.dto.LoginRequest;
import ca.ulaval.glo4003.trotti.interfaces.rest.user.dto.LoginResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.user.dto.RegisterResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final ActivateAccountUseCase activateAccountUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final TokenGenerator tokenGenerator;

    public UserController() {
        ApplicationContext context = ApplicationContext.getInstance();
        this.registerUserUseCase = context.getRegisterUserUseCase();
        this.loginUserUseCase = context.getLoginUserUseCase();
        this.activateAccountUseCase = context.getActivateAccountUseCase();
        this.getAllUsersUseCase = context.getGetAllUsersUseCase();
        this.tokenGenerator = context.getTokenGenerator();
    }

    @POST
    @Path("/register")
    public Response register(UserRegistrationRequest request) {
        try {
            registerUserUseCase.execute(
                    request.getEmail(),
                    request.getPassword(),
                    request.getName(),
                    request.getDateOfBirth(),
                    request.getGender(),
                    request.getIdul()
            );

            ca.ulaval.glo4003.trotti.interfaces.rest.user.model.User userDto = new ca.ulaval.glo4003.trotti.interfaces.rest.user.model.User();
            userDto.setName(request.getName());
            userDto.setEmail(request.getEmail());
            userDto.setDateOfBirth(request.getDateOfBirth());
            userDto.setGender(request.getGender());
            userDto.setIdul(request.getIdul());

            return Response.status(Response.Status.CREATED)
                    .entity(new RegisterResponse(userDto))
                    .build();
        } catch (DomainException e) {
            return ErrorResponseBuilder.buildErrorResponse(e);
        } catch (IllegalArgumentException e) {
            return ErrorResponseBuilder.buildErrorResponse(e, Response.Status.BAD_REQUEST);
        }
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        try {
            if (request == null || request.getEmail() == null || request.getPassword() == null) {
                return ErrorResponseBuilder.buildErrorResponse(
                    new IllegalArgumentException("Email and password required."),
                    Response.Status.BAD_REQUEST
                );
            }

            String token = loginUserUseCase.execute(request.getEmail(), request.getPassword());
            return Response.ok(new LoginResponse(token)).build();
        } catch (DomainException e) {
            return ErrorResponseBuilder.buildErrorResponse(e, Response.Status.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            return ErrorResponseBuilder.buildErrorResponse(e, Response.Status.BAD_REQUEST);
        }
    }

    @GET
    @Path("/all")
    public Response getAllUsers() {
        try {
            List<User> users = getAllUsersUseCase.execute();

            // Convert domain users to DTOs
            List<ca.ulaval.glo4003.trotti.interfaces.rest.user.model.User> userDtos = users.stream()
                .map(user -> {
                    ca.ulaval.glo4003.trotti.interfaces.rest.user.model.User dto = new ca.ulaval.glo4003.trotti.interfaces.rest.user.model.User();
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail().getValue());
                    dto.setDateOfBirth(user.getDateOfBirth().toString());
                    dto.setGender(user.getGender().toString());
                    dto.setIdul(user.getIdul().getValue());
                    if (user.isActivated()) {
                        dto.activate();
                    }
                    return dto;
                })
                .toList();

            return Response.ok(userDtos).build();
        } catch (Exception e) {
            return ErrorResponseBuilder.buildInternalServerError("Can't get all users");
        }
    }

    @POST
    @Path("/activate")
    public Response activateAccount(@HeaderParam("Authorization") String authHeader) {
        try {
            String token = AuthorizationHeaderExtractor.extractToken(authHeader);
            String email = tokenGenerator.extractEmail(token);
            activateAccountUseCase.execute(email);
            return Response.ok(new AccountActivationResponse("Account activated")).build();
        } catch (DomainException e) {
            return ErrorResponseBuilder.buildErrorResponse(e);
        } catch (Exception e) {
            return ErrorResponseBuilder.buildInternalServerError(e);
        }
    }

    public static class UserRegistrationRequest {
        private String name;
        private String dateOfBirth;
        private String gender;
        private String idul;
        private String email;
        private String password;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }

        public String getIdul() { return idul; }
        public void setIdul(String idul) { this.idul = idul; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}

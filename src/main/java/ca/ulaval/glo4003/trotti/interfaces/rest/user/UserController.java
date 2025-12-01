package ca.ulaval.glo4003.trotti.interfaces.rest.user;

import ca.ulaval.glo4003.trotti.application.billing.UpdateAutoPayPreferenceUseCase;
import ca.ulaval.glo4003.trotti.application.pass.PassPurchaseResult;
import ca.ulaval.glo4003.trotti.application.user.ActivateAccountUseCase;
import ca.ulaval.glo4003.trotti.application.user.GetAllUsersUseCase;
import ca.ulaval.glo4003.trotti.application.user.GetUserBalanceUseCase;
import ca.ulaval.glo4003.trotti.application.user.LoginUserUseCase;
import ca.ulaval.glo4003.trotti.application.user.RegisterUserUseCase;
import ca.ulaval.glo4003.trotti.application.user.dto.UserDTO;
import ca.ulaval.glo4003.trotti.domain.pass.Session;
import ca.ulaval.glo4003.trotti.domain.shared.DomainException;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.interfaces.rest.config.ApplicationContext;
import ca.ulaval.glo4003.trotti.interfaces.rest.error.ErrorResponseBuilder;
import ca.ulaval.glo4003.trotti.interfaces.rest.user.dto.*;
import ca.ulaval.glo4003.trotti.interfaces.rest.pass.dto.PurchaseRequest;
import ca.ulaval.glo4003.trotti.interfaces.rest.pass.dto.PurchaseResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.security.AuthenticationService;
import ca.ulaval.glo4003.trotti.interfaces.rest.security.AuthorizationHeaderExtractor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import java.util.List;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final ActivateAccountUseCase activateAccountUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUserBalanceUseCase getUserBalanceUseCase;
    private final AuthenticationService authenticationService;
    private final UpdateAutoPayPreferenceUseCase updateAutoPayPreferenceUseCase;

    public UserController() {
        ApplicationContext context = ApplicationContext.getInstance();
        this.registerUserUseCase = context.getRegisterUserUseCase();
        this.loginUserUseCase = context.getLoginUserUseCase();
        this.activateAccountUseCase = context.getActivateAccountUseCase();
        this.getAllUsersUseCase = context.getGetAllUsersUseCase();
        this.getUserBalanceUseCase = context.getGetUserBalanceUseCase();
        this.authenticationService = context.getAuthenticationService();
        this.updateAutoPayPreferenceUseCase = context.getUpdateAutoPayPreferenceUseCase();
    }

    @POST
    @Path("/register")
    public Response register(UserRegistrationRequest request) {
        try {
            UserDTO userDTO = registerUserUseCase.execute(
                    request.getEmail(),
                    request.getPassword(),
                    request.getName(),
                    request.getDateOfBirth(),
                    request.getGender(),
                    request.getIdul(),
                    Role.USER);

            ca.ulaval.glo4003.trotti.interfaces.rest.user.model.User userResponseModel = new ca.ulaval.glo4003.trotti.interfaces.rest.user.model.User();
            userResponseModel.setName(userDTO.getName());
            userResponseModel.setEmail(userDTO.getEmail());
            userResponseModel.setDateOfBirth(userDTO.getDateOfBirth());
            userResponseModel.setGender(userDTO.getGender());
            userResponseModel.setIdul(userDTO.getIdul());

            return Response.status(Response.Status.CREATED)
                    .entity(new RegisterResponse(userResponseModel))
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
                        Response.Status.BAD_REQUEST);
            }

            ca.ulaval.glo4003.trotti.application.auth.AuthResult authResult = loginUserUseCase
                    .execute(request.getEmail(), request.getPassword());
            return Response.ok(new LoginResponse(authResult.getToken())).build();
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
            List<UserDTO> users = getAllUsersUseCase.execute();

            List<ca.ulaval.glo4003.trotti.interfaces.rest.user.model.User> userDtos = users.stream()
                    .map(userDTO -> {
                        ca.ulaval.glo4003.trotti.interfaces.rest.user.model.User dto = new ca.ulaval.glo4003.trotti.interfaces.rest.user.model.User();
                        dto.setName(userDTO.getName());
                        dto.setEmail(userDTO.getEmail());
                        dto.setDateOfBirth(userDTO.getDateOfBirth());
                        dto.setGender(userDTO.getGender());
                        dto.setIdul(userDTO.getIdul());
                        if (userDTO.isActivated()) {
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
            String email = authenticationService.extractEmailFromToken(token);
            activateAccountUseCase.execute(email);
            return Response.ok(new AccountActivationResponse("Account activated")).build();
        } catch (DomainException e) {
            return ErrorResponseBuilder.buildErrorResponse(e);
        } catch (Exception e) {
            return ErrorResponseBuilder.buildInternalServerError(e);
        }
    }

    @GET
    @Path("/balance")
    public Response getUserBalance(@HeaderParam("Authorization") String authHeader) {
        try {
            String token = AuthorizationHeaderExtractor.extractToken(authHeader);
            String email = authenticationService.extractEmailFromToken(token);

            BigDecimal amount = getUserBalanceUseCase.execute(email);

            return Response.ok(new GetUserBalanceResponse(amount)).build();
        } catch (DomainException e) {
            return ErrorResponseBuilder.buildErrorResponse(e);
        } catch (Exception e) {
            return ErrorResponseBuilder.buildInternalServerError(e);
        }
    }

    @POST
    @Path("/balance/pay")
    public Response purchase(@HeaderParam("Authorization") String authHeader) {
        try {
            String token = AuthorizationHeaderExtractor.extractToken(authHeader);
            String email = authenticationService.extractEmailFromToken(token);

            // TODO: Make a real invoice
            InvoiceDto invoice = new InvoiceDto(
                    new BigDecimal(0), true, LocalDate.of(2025, Month.NOVEMBER, 1), LocalDate.of(2025, Month.NOVEMBER, 30));

            return Response.status(Response.Status.CREATED)
                    .entity(new PaymentResponse("Purchase successful", invoice))
                    .build();
        } catch (DomainException e) {
            return ErrorResponseBuilder.buildErrorResponse(e);
        } catch (IllegalArgumentException e) {
            return ErrorResponseBuilder.buildErrorResponse(e, Response.Status.BAD_REQUEST);
        } catch (Exception e) {
            return ErrorResponseBuilder.buildInternalServerError(e);
        }
    }

    @PUT
    @Path("/settings/auto-pay")
    public Response updateAutoPayPreference(@HeaderParam("Authorization") String authHeader, AutoPayRequest request) {
        try {
            String token = AuthorizationHeaderExtractor.extractToken(authHeader);
            String email = authenticationService.extractEmailFromToken(token);

            updateAutoPayPreferenceUseCase.execute(email, request.isEnabled());

            return Response.ok().build();
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getIdul() {
            return idul;
        }

        public void setIdul(String idul) {
            this.idul = idul;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}

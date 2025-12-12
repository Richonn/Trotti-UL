package ca.ulaval.glo4003.trotti.interfaces.rest.pass;

import ca.ulaval.glo4003.trotti.application.pass.GetUserPassesUseCase;
import ca.ulaval.glo4003.trotti.application.pass.dto.PassPurchaseResult;
import ca.ulaval.glo4003.trotti.application.pass.PurchasePassUseCase;
import ca.ulaval.glo4003.trotti.application.pass.dto.PassDTO;
import ca.ulaval.glo4003.trotti.domain.pass.Session;
import ca.ulaval.glo4003.trotti.interfaces.rest.config.ApplicationContext;
import ca.ulaval.glo4003.trotti.application.auth.AuthUser;
import ca.ulaval.glo4003.trotti.interfaces.rest.security.AuthenticationService;
import ca.ulaval.glo4003.trotti.interfaces.rest.pass.response.InvoiceResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.pass.response.PassResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.pass.request.PurchaseRequest;
import ca.ulaval.glo4003.trotti.interfaces.rest.pass.response.PurchaseResponse;
import ca.ulaval.glo4003.trotti.interfaces.rest.pass.mapper.PassResponseMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

@Path("/pass")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PassController {
    private final PurchasePassUseCase purchasePassUseCase;
    private final GetUserPassesUseCase getUserPassesUseCase;
    private final AuthenticationService authenticationService;

    public PassController() {
        ApplicationContext context = ApplicationContext.getInstance();
        this.purchasePassUseCase = context.getPurchasePassUseCase();
        this.getUserPassesUseCase = context.getGetUserPassesUseCase();
        this.authenticationService = context.getAuthenticationService();
    }

    @POST
    @Path("/purchase")
    public Response purchase(@Context SecurityContext securityContext, PurchaseRequest request) {
        AuthUser authUser = (AuthUser) securityContext.getUserPrincipal();

        PassPurchaseResult result = purchasePassUseCase.execute(
                authUser.getEmail(),
                request.getSession(),
                request.getExtraMinutesPerDay(),
                request.isMonthlyBilling(),
                request.getCardNumber(),
                request.getCardHolder(),
                request.getExpiryDate(),
                request.getCvv()
        );

        InvoiceResponse invoice = PassResponseMapper.toInvoiceDto(result);

        return Response.status(Response.Status.CREATED)
                .entity(new PurchaseResponse("Purchase successful", invoice))
                .build();
    }

    @GET
    @Path("/my-purchases")
    public Response getMyPurchases(@Context SecurityContext securityContext) {
        AuthUser authUser = (AuthUser) securityContext.getUserPrincipal();

        List<PassDTO> passes = getUserPassesUseCase.execute(authUser.getEmail());
        List<PassResponse> passDtos = PassResponseMapper.toDtoList(passes);
        return Response.ok(passDtos).build();
    }
}

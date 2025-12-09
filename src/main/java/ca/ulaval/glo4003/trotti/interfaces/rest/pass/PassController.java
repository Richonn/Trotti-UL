package ca.ulaval.glo4003.trotti.interfaces.rest.pass;

import ca.ulaval.glo4003.trotti.application.pass.GetUserPassesUseCase;
import ca.ulaval.glo4003.trotti.application.pass.PassPurchaseResult;
import ca.ulaval.glo4003.trotti.application.pass.PurchasePassUseCase;
import ca.ulaval.glo4003.trotti.application.pass.dto.PassDTO;
import ca.ulaval.glo4003.trotti.domain.pass.Session;
import ca.ulaval.glo4003.trotti.domain.shared.DomainException;
import ca.ulaval.glo4003.trotti.interfaces.rest.config.ApplicationContext;
import ca.ulaval.glo4003.trotti.interfaces.rest.error.ErrorResponseBuilder;
import ca.ulaval.glo4003.trotti.application.auth.AuthUser;
import ca.ulaval.glo4003.trotti.interfaces.rest.security.AuthenticationService;
import ca.ulaval.glo4003.trotti.interfaces.rest.pass.dto.InvoiceDto;
import ca.ulaval.glo4003.trotti.interfaces.rest.pass.dto.PurchaseRequest;
import ca.ulaval.glo4003.trotti.interfaces.rest.pass.dto.PurchaseResponse;
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
        try {
            AuthUser authUser = (AuthUser) securityContext.getUserPrincipal();

            PassPurchaseResult result = purchasePassUseCase.execute(
                    authUser.getEmail(),
                    request.getSession().name(),
                    request.getExtraMinutesPerDay(),
                    request.isMonthlyBilling(),
                    request.getCardNumber(),
                    request.getCardHolder(),
                    request.getExpiryDate(),
                    request.getCvv()
            );

            InvoiceDto invoice = PassResponseMapper.toInvoiceDto(result);

            return Response.status(Response.Status.CREATED)
                    .entity(new PurchaseResponse("Purchase successful", invoice))
                    .build();
        } catch (DomainException e) {
            return ErrorResponseBuilder.buildErrorResponse(e);
        } catch (IllegalArgumentException e) {
            return ErrorResponseBuilder.buildErrorResponse(e, Response.Status.BAD_REQUEST);
        } catch (Exception e) {
            return ErrorResponseBuilder.buildInternalServerError(e);
        }
    }

    @GET
    @Path("/my-purchases")
    public Response getMyPurchases(@Context SecurityContext securityContext) {
        try {
            AuthUser authUser = (AuthUser) securityContext.getUserPrincipal();

            List<PassDTO> passes = getUserPassesUseCase.execute(authUser.getEmail());
            List<ca.ulaval.glo4003.trotti.interfaces.rest.pass.dto.PassDto> passDtos = PassResponseMapper.toDtoList(passes);
            return Response.ok(passDtos).build();
        } catch (DomainException e) {
            return ErrorResponseBuilder.buildErrorResponse(e);
        } catch (Exception e) {
            return ErrorResponseBuilder.buildInternalServerError(e);
        }
    }
}

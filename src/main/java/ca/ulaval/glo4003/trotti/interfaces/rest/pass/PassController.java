package ca.ulaval.glo4003.trotti.interfaces.rest.pass;

import ca.ulaval.glo4003.trotti.application.pass.GetUserPassesUseCase;
import ca.ulaval.glo4003.trotti.application.pass.PassPurchaseResult;
import ca.ulaval.glo4003.trotti.application.pass.PurchasePassUseCase;
import ca.ulaval.glo4003.trotti.domain.pass.Pass;
import ca.ulaval.glo4003.trotti.domain.pass.Session;
import ca.ulaval.glo4003.trotti.domain.shared.DomainException;
import ca.ulaval.glo4003.trotti.interfaces.rest.config.ApplicationContext;
import ca.ulaval.glo4003.trotti.interfaces.rest.pass.dto.InvoiceDto;
import ca.ulaval.glo4003.trotti.interfaces.rest.pass.dto.PassDto;
import ca.ulaval.glo4003.trotti.interfaces.rest.pass.dto.PurchaseRequest;
import ca.ulaval.glo4003.trotti.interfaces.rest.pass.dto.PurchaseResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/pass")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PassController {
    private final PurchasePassUseCase purchasePassUseCase;
    private final GetUserPassesUseCase getUserPassesUseCase;

    public PassController() {
        ApplicationContext context = ApplicationContext.getInstance();
        this.purchasePassUseCase = context.getPurchasePassUseCase();
        this.getUserPassesUseCase = context.getGetUserPassesUseCase();
    }

    @POST
    @Path("/purchase")
    public Response purchase(@HeaderParam("Authorization") String authHeader, PurchaseRequest request) {
        try {
            String token = authHeader.substring(7);
            String email = ApplicationContext.getInstance().getJwtTokenService().extractEmail(token);

            PassPurchaseResult result = purchasePassUseCase.execute(
                    email,
                    request.getSession().name(),
                    request.getExtraMinutesPerDay(),
                    request.isMonthlyBilling(),
                    request.getCardNumber(),
                    request.getCardHolder(),
                    request.getExpiryDate(),
                    request.getCvv()
            );

            Session sessionEnum = Session.getInstance(result.getSessionCode());

            InvoiceDto invoice = new InvoiceDto(
                    sessionEnum.name(),
                    result.getTotalPrice(),
                    result.isMonthlyBilling(),
                    result.getStartDate(),
                    result.getEndDate()
            );

            return Response.ok(new PurchaseResponse("Purchase successful", invoice)).build();
        } catch (DomainException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/my-purchases")
    public Response getMyPurchases(@HeaderParam("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = ApplicationContext.getInstance().getJwtTokenService().extractEmail(token);

            List<Pass> passes = getUserPassesUseCase.execute(email);

            List<PassDto> passDtos = passes.stream()
                    .map(pass -> new PassDto(
                            pass.getId(),
                            pass.getSession().name(),
                            pass.getExtraMinutesPerDay(),
                            pass.isMonthlyBilling(),
                            pass.getTotalPrice(),
                            pass.isActive()
                    ))
                    .toList();

            return Response.ok(passDtos).build();
        } catch (DomainException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}

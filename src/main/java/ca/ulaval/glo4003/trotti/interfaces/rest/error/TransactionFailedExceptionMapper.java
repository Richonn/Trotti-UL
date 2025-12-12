package ca.ulaval.glo4003.trotti.interfaces.rest.error;

import ca.ulaval.glo4003.trotti.domain.shared.TransactionFailedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TransactionFailedExceptionMapper implements ExceptionMapper<TransactionFailedException> {
    @Override
    public Response toResponse(TransactionFailedException exception) {
        ErrorResponseBuilder.ErrorResponse error = new ErrorResponseBuilder.ErrorResponse(exception.getMessage());
        return Response.status(Response.Status.PAYMENT_REQUIRED).entity(error).build();
    }
}

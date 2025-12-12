package ca.ulaval.glo4003.trotti.interfaces.rest.error;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    @Override
    public Response toResponse(IllegalArgumentException exception) {
        ErrorResponseBuilder.ErrorResponse error = new ErrorResponseBuilder.ErrorResponse(exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }
}

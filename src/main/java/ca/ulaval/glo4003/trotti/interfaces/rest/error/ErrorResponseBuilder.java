package ca.ulaval.glo4003.trotti.interfaces.rest.error;

import ca.ulaval.glo4003.trotti.domain.shared.DomainException;
import jakarta.ws.rs.core.Response;

public class ErrorResponseBuilder {

    public static Response buildErrorResponse(DomainException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
    }

    public static Response buildErrorResponse(DomainException e, Response.Status status) {
        return Response.status(status)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
    }

    public static Response buildErrorResponse(Exception e, Response.Status status) {
        return Response.status(status)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
    }

    public static Response buildInternalServerError(String message) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse(message))
                .build();
    }

    public static Response buildInternalServerError(Exception e) {
        return buildInternalServerError(e.getMessage());
    }

    public static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}

package ca.ulaval.glo4003.trotti.interfaces.rest.error;

import ca.ulaval.glo4003.trotti.domain.pass.exception.NoActivePassException;
import ca.ulaval.glo4003.trotti.domain.ride.exception.ExpiredRideCodeException;
import ca.ulaval.glo4003.trotti.domain.ride.exception.InvalidRideCodeException;
import ca.ulaval.glo4003.trotti.domain.ride.exception.NoActiveRideException;
import ca.ulaval.glo4003.trotti.domain.ride.exception.PaymentDeclinedException;
import ca.ulaval.glo4003.trotti.domain.shared.DomainException;
import ca.ulaval.glo4003.trotti.domain.station.exception.StationNotFoundException;
import ca.ulaval.glo4003.trotti.domain.user.exception.AccountNotActivatedException;
import ca.ulaval.glo4003.trotti.domain.user.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.trotti.domain.user.exception.UnpaidInvoiceException;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;

@Provider
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {
    private static final Map<Class<? extends DomainException>, Response.Status> STATUS_MAP = new HashMap<>();

    static {
        STATUS_MAP.put(UserNotFoundException.class, Response.Status.NOT_FOUND);
        STATUS_MAP.put(StationNotFoundException.class, Response.Status.NOT_FOUND);

        STATUS_MAP.put(AccountNotActivatedException.class, Response.Status.FORBIDDEN);
        STATUS_MAP.put(NoActivePassException.class, Response.Status.FORBIDDEN);
        STATUS_MAP.put(InvalidRideCodeException.class, Response.Status.FORBIDDEN);
        STATUS_MAP.put(ExpiredRideCodeException.class, Response.Status.FORBIDDEN);
        STATUS_MAP.put(UnpaidInvoiceException.class, Response.Status.FORBIDDEN);

        STATUS_MAP.put(InvalidCredentialsException.class, Response.Status.UNAUTHORIZED);

        STATUS_MAP.put(PaymentDeclinedException.class, Response.Status.PAYMENT_REQUIRED);

        STATUS_MAP.put(NoActiveRideException.class, Response.Status.BAD_REQUEST);
    }

    @Override
    public Response toResponse(DomainException exception) {
        Response.Status status = STATUS_MAP.getOrDefault(
                exception.getClass(),
                Response.Status.BAD_REQUEST
        );

        ErrorResponseBuilder.ErrorResponse error = new ErrorResponseBuilder.ErrorResponse(exception.getMessage());

        return Response.status(status).entity(error).build();
    }
}

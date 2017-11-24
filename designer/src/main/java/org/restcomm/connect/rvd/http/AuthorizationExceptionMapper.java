package org.restcomm.connect.rvd.http;

import org.restcomm.connect.rvd.exceptions.AuthorizationException;
import org.restcomm.connect.rvd.exceptions.ForbiddenResourceException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author Orestis Tsakiridis
 */
public class AuthorizationExceptionMapper implements ExceptionMapper<AuthorizationException> {
    @Override
    public Response toResponse(AuthorizationException e) {
        if (e instanceof ForbiddenResourceException)
            return Response.status(Response.Status.FORBIDDEN).build();
        else
            return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}

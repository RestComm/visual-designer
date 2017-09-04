package org.restcomm.connect.rvd.http;

import com.sun.jersey.api.NotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Better handling when a resource does not exists. Without it a 500 is returned and an exception is thrown.
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class ResourceNotFoundMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}

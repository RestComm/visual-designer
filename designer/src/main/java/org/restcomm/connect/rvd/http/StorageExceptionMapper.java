package org.restcomm.connect.rvd.http;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import java.util.logging.Level;

//@Provider - needed only if package scanning is enabled
public class StorageExceptionMapper implements ExceptionMapper<StorageException> {

    @Override
    public Response toResponse(StorageException e) {
        if (RvdLoggers.system.isLoggable(Level.SEVERE))
            RvdLoggers.system.log(Level.SEVERE, e.getMessage(), e);

        RvdResponse rvdResponse = new RvdResponse(RvdResponse.Status.ERROR).setException(e);
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(rvdResponse.asJson()).build();
    }

}

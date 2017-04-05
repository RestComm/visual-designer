package org.restcomm.connect.rvd.http;


import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.restcomm.connect.rvd.exceptions.ProjectDoesNotExist;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;

import java.util.logging.Level;

//@Provider - needed only if package scanning is enabled
public class ProjectDoesNotExistMapper implements ExceptionMapper<ProjectDoesNotExist> {

    @Override
    public Response toResponse(ProjectDoesNotExist e) {
        if (RvdLoggers.local.isLoggable(Level.WARNING))
            RvdLoggers.local.log(Level.WARNING, LoggingContext.buildPrefix(e.getAccountSid(),e.getApplicationSid()) + (e.getMessage() != null ? e.getMessage(): ""), e);

        RvdResponse rvdResponse = new RvdResponse(RvdResponse.Status.ERROR).setException(e);
        return Response.status(Status.NOT_FOUND).entity(rvdResponse.asJson()).build();
    }

}


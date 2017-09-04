package org.restcomm.connect.rvd.http;


import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.restcomm.connect.rvd.exceptions.ProjectDoesNotExist;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;

import org.apache.log4j.Level;

//@Provider - needed only if package scanning is enabled
public class ProjectDoesNotExistMapper implements ExceptionMapper<ProjectDoesNotExist> {

    @Override
    public Response toResponse(ProjectDoesNotExist e) {
        RvdLoggers.local.log(Level.WARN, LoggingContext.buildPrefix(e.getAccountSid(),e.getApplicationSid(), e.getCallSid()) + " Exception " + e.getClass().getName() + ": " + (e.getMessage() != null ? e.getMessage(): "") + ". Thrown at: " + e.getStackTrace()[0]);
        RvdResponse rvdResponse = new RvdResponse(RvdResponse.Status.ERROR).setExceptionInfo(e);
        return Response.status(Status.NOT_FOUND).entity(rvdResponse.asJson()).build();
    }

}


package org.restcomm.connect.rvd;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.restcomm.connect.rvd.logging.ProjectLogger;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.model.ModelMarshaler;
import org.restcomm.connect.rvd.storage.WorkspaceStorage;


public class RvdContext {

    private ModelMarshaler marshaler;
    private RvdConfiguration configuration;
    private ServletContext servletContext;
    protected WorkspaceStorage workspaceStorage;
    public LoggingContext logging;

    public RvdContext(HttpServletRequest request, ServletContext servletContext, RvdConfiguration config, LoggingContext logging) {
        if (request == null || servletContext == null)
            throw new IllegalArgumentException();
        this.configuration = config;
        this.marshaler = new ModelMarshaler();
        this.workspaceStorage = new WorkspaceStorage(configuration.getWorkspaceBasePath(), marshaler);
        this.servletContext = servletContext;
        this.logging = logging;
    }

    public ProjectLogger getProjectLogger() {
        throw new UnsupportedOperationException("You'll need a ProjectAwareRvdContext to use ProjectLogger");
    }

    public ModelMarshaler getMarshaler() {
        return marshaler;
    }

    public RvdConfiguration getConfiguration() {
        return configuration;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public WorkspaceStorage getWorkspaceStorage() {
        return workspaceStorage;
    }

}

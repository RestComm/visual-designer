package org.restcomm.connect.rvd;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.restcomm.connect.rvd.logging.ProjectLogger;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.model.StepMarshaler;
import org.restcomm.connect.rvd.storage.FsWorkspaceStorage;
import org.restcomm.connect.rvd.storage.JsonModelStorage;


public class RvdContext {

    private StepMarshaler marshaler;
    private RvdConfiguration configuration;
    private ServletContext servletContext;
    protected JsonModelStorage storage;
    public LoggingContext logging;

    public RvdContext(HttpServletRequest request, ServletContext servletContext, RvdConfiguration config, LoggingContext logging) {
        if (request == null || servletContext == null)
            throw new IllegalArgumentException();
        this.configuration = config;
        this.marshaler = new StepMarshaler();
        this.storage = new JsonModelStorage(new FsWorkspaceStorage(configuration.getWorkspaceBasePath()), marshaler);
        this.servletContext = servletContext;
        this.logging = logging;
    }

    public ProjectLogger getProjectLogger() {
        throw new UnsupportedOperationException("You'll need a ProjectAwareRvdContext to use ProjectLogger");
    }

    public StepMarshaler getMarshaler() {
        return marshaler;
    }

    public RvdConfiguration getConfiguration() {
        return configuration;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public JsonModelStorage getWorkspaceStorage() {
        return storage;
    }

}

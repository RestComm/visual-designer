package org.restcomm.connect.rvd;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.restcomm.connect.rvd.logging.ProjectLogger;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.model.StepMarshaler;
import org.restcomm.connect.rvd.storage.OldWorkspaceStorage;


public class RvdContext {

    private StepMarshaler marshaler;
    private RvdConfiguration configuration;
    private ServletContext servletContext;
    protected OldWorkspaceStorage oldWorkspaceStorage;
    public LoggingContext logging;

    public RvdContext(HttpServletRequest request, ServletContext servletContext, RvdConfiguration config, LoggingContext logging) {
        if (request == null || servletContext == null)
            throw new IllegalArgumentException();
        this.configuration = config;
        this.marshaler = new StepMarshaler();
        this.oldWorkspaceStorage = new OldWorkspaceStorage(configuration.getWorkspaceBasePath(), marshaler);
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

    public OldWorkspaceStorage getWorkspaceStorage() {
        return oldWorkspaceStorage;
    }

}

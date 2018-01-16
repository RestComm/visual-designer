package org.restcomm.connect.rvd.http.resources;

import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.storage.ProjectDao;
import org.restcomm.connect.rvd.storage.WorkspaceStorage;

import javax.servlet.http.HttpServletRequest;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class ProjectRestServiceMocked extends ProjectRestService {

    ProjectDao projectDao;

    public ProjectRestServiceMocked(ProjectDao projectDao, HttpServletRequest request, LoggingContext logging) {
        super();
        this.projectDao = projectDao;
        this.request = request;
        this.logging = logging;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    protected void secure() {
        // do nothing
    }

    @Override
    protected ProjectDao buildProjectDao(WorkspaceStorage storage) {
        return projectDao;
    }
}

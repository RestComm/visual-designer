package org.restcomm.connect.rvd.upgrade;

import org.restcomm.connect.rvd.storage.WorkspaceDao;

/**
 * Utility class that helps maintain and upgrade a workspace
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class WorkspaceMaintainer {

    WorkspaceDao workspaceDao;

    public WorkspaceMaintainer(WorkspaceDao workspaceDao) {
        this.workspaceDao = workspaceDao;
    }
}

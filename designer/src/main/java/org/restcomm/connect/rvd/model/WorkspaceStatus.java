package org.restcomm.connect.rvd.model;

/**
 * Model class for the root status of the workspace
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class WorkspaceStatus {
    String version;

    public WorkspaceStatus(String version) {
        this.version = version;
    }

    public WorkspaceStatus() {
    }

    public String getVersion() {
        return version;
    }
}

package org.restcomm.connect.rvd.model;

/**
 * Model class for the root status of the workspace
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class WorkspaceStatus {
    Integer version;

    public WorkspaceStatus(Integer version) {
        this.version = version;
    }

    public WorkspaceStatus() {
    }

    public Integer getVersion() {
        return version;
    }
}

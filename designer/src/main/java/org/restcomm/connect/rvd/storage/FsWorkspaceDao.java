package org.restcomm.connect.rvd.storage;

import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.model.WorkspaceStatus;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class FsWorkspaceDao implements WorkspaceDao {

    JsonModelStorage workspaceStorage;
    RvdConfiguration configuration;

    public FsWorkspaceDao(JsonModelStorage workspaceStorage, RvdConfiguration configuration) {
        this.workspaceStorage = workspaceStorage;
        this.configuration = configuration;
    }

    public FsWorkspaceDao(JsonModelStorage workspaceStorage) {
        this.workspaceStorage = workspaceStorage;
    }

    @Override
    public WorkspaceStatus loadWorkspaceStatus() {
        try {
            WorkspaceStatus status = workspaceStorage.loadEntity("status","", WorkspaceStatus.class);
            return status;
        } catch (StorageException e) {
            return null;
        }
    }
}

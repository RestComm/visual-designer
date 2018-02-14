package org.restcomm.connect.rvd.storage;

import org.restcomm.connect.rvd.model.WorkspaceStatus;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

/**
 * Dao class for workspace maintainance operations like upgrading, copying templates etc.
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public interface WorkspaceDao {

    WorkspaceStatus loadWorkspaceStatus() throws StorageException;
    void storeWorkspaceStatus(WorkspaceStatus workspaceStatus) throws StorageException;
}

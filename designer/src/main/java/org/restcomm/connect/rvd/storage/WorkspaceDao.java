package org.restcomm.connect.rvd.storage;

import org.restcomm.connect.rvd.model.WorkspaceStatus;

/**
 * Dao class for workspace maintainance operations like upgrading, copying templates etc.
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public interface WorkspaceDao {

    WorkspaceStatus loadWorkspaceStatus();
}

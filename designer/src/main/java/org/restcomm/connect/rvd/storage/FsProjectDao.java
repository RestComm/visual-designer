package org.restcomm.connect.rvd.storage;

import org.restcomm.connect.rvd.model.project.Node;
import org.restcomm.connect.rvd.model.server.ProjectOptions;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class FsProjectDao implements ProjectDao {

    String applicationName;
    WorkspaceStorage workspaceStorage;

    public FsProjectDao(String applicationName, WorkspaceStorage workspaceStorage) {
        this.applicationName = applicationName;
        this.workspaceStorage = workspaceStorage;
    }

    @Override
    public ProjectOptions loadProjectOptions() throws StorageException {
        return FsProjectStorage.loadProjectOptions(applicationName, workspaceStorage);
    }

    @Override
    public boolean hasBootstrapInfo() {
        return FsProjectStorage.hasBootstrapInfo(applicationName,workspaceStorage);
    }

    @Override
    public Node loadNode(String moduleName) throws StorageException {
        return FsProjectStorage.loadNode(applicationName,moduleName,workspaceStorage);
    }

    @Override
    public String loadBootstrapInfo() throws StorageException {
        return FsProjectStorage.loadBootstrapInfo(applicationName, workspaceStorage);
    }


}

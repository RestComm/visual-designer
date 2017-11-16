package org.restcomm.connect.rvd.storage;

import org.restcomm.connect.rvd.model.project.Node;
import org.restcomm.connect.rvd.model.server.ProjectOptions;
import org.restcomm.connect.rvd.storage.exceptions.StorageEntityNotFound;
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
    public Node loadNode(String moduleName) throws StorageException {
        return FsProjectStorage.loadNode(applicationName,moduleName,workspaceStorage);
    }

    /**
     * Returns current project's bootstrap information as a JSON string. If it does not exist it returns null.
     *
     * @return a JSON block as a string of null
     * @throws StorageException
     */
    @Override
    public String loadBootstrapInfo() throws StorageException {
        try {
            return FsProjectStorage.loadBootstrapInfo(applicationName, workspaceStorage);
        } catch (StorageEntityNotFound e) {
            return null;
        }
    }


}

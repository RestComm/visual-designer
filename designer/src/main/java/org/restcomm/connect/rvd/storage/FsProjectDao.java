package org.restcomm.connect.rvd.storage;

import org.restcomm.connect.rvd.model.ProjectSettings;
import org.restcomm.connect.rvd.model.project.Node;
import org.restcomm.connect.rvd.model.project.ProjectState;
import org.restcomm.connect.rvd.model.server.ProjectOptions;
import org.restcomm.connect.rvd.storage.exceptions.StorageEntityNotFound;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class FsProjectDao implements ProjectDao {

    WorkspaceStorage workspaceStorage;

    public FsProjectDao(WorkspaceStorage workspaceStorage) {
//        if (RvdUtils.isEmpty(applicationName)) {
//            throw new IllegalStateException("Application name is null. Cannot create FsProjectDao");
//        }
//        this.applicationName = applicationName;
        this.workspaceStorage = workspaceStorage;
    }

    @Override
    public ProjectState loadProject(String applicationId) throws StorageException {
        try {
            return FsProjectStorage.loadProject(applicationId, workspaceStorage);
        } catch (StorageEntityNotFound e) {
            return null;
        }
    }

    @Override
    public ProjectOptions loadProjectOptions(String applicationId) throws StorageException {
        return FsProjectStorage.loadProjectOptions(applicationId, workspaceStorage);
    }

    @Override
    public Node loadNode(String moduleName, String applicationId) throws StorageException {
        return FsProjectStorage.loadNode(applicationId,moduleName,workspaceStorage);
    }

    @Override
    public String loadBootstrapInfo(String applicationId) throws StorageException {
        try {
            return FsProjectStorage.loadBootstrapInfo(applicationId, workspaceStorage);
        } catch (StorageEntityNotFound e) {
            return null;
        }
    }

    @Override
    public ProjectSettings loadSettings(String applicationId) throws StorageException {
        try {
            return FsProjectStorage.loadProjectSettings(applicationId, workspaceStorage);
        }   catch (StorageEntityNotFound e) {
            return null;
        }
    }

    @Override
    public void storeSettings(ProjectSettings projectSettings, String applicationId) throws StorageException {
        FsProjectStorage.storeProjectSettings(projectSettings, applicationId, workspaceStorage);
    }

    @Override
    public String loadProjectStateRaw(String applicationId) throws StorageException {
        return FsProjectStorage.loadProjectString(applicationId, workspaceStorage);
    }


}

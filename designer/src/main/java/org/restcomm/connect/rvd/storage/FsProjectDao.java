package org.restcomm.connect.rvd.storage;

import org.restcomm.connect.rvd.model.ProjectSettings;
import org.restcomm.connect.rvd.model.project.Node;
import org.restcomm.connect.rvd.model.project.ProjectState;
import org.restcomm.connect.rvd.model.server.ProjectOptions;
import org.restcomm.connect.rvd.storage.exceptions.StorageEntityNotFound;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.utils.RvdUtils;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class FsProjectDao implements ProjectDao {

    String applicationName;
    WorkspaceStorage workspaceStorage;

    public FsProjectDao(String applicationName, WorkspaceStorage workspaceStorage) {
        if (RvdUtils.isEmpty(applicationName)) {
            throw new IllegalStateException("Application name is null. Cannot create FsProjectDao");
        }
        this.applicationName = applicationName;
        this.workspaceStorage = workspaceStorage;
    }

    @Override
    public String getName() {
        return applicationName;
    }

    @Override
    public ProjectState loadProject() throws StorageException {
        try {
            return FsProjectStorage.loadProject(applicationName, workspaceStorage);
        } catch (StorageEntityNotFound e) {
            return null;
        }
    }

    @Override
    public ProjectOptions loadProjectOptions() throws StorageException {
        return FsProjectStorage.loadProjectOptions(applicationName, workspaceStorage);
    }

    @Override
    public Node loadNode(String moduleName) throws StorageException {
        return FsProjectStorage.loadNode(applicationName,moduleName,workspaceStorage);
    }

    @Override
    public String loadBootstrapInfo() throws StorageException {
        try {
            return FsProjectStorage.loadBootstrapInfo(applicationName, workspaceStorage);
        } catch (StorageEntityNotFound e) {
            return null;
        }
    }

    @Override
    public ProjectSettings loadSettings() throws StorageException {
        try {
            return FsProjectStorage.loadProjectSettings(applicationName, workspaceStorage);
        }   catch (StorageEntityNotFound e) {
            return null;
        }
    }

    @Override
    public void storeSettings(ProjectSettings projectSettings) throws StorageException {
        FsProjectStorage.storeProjectSettings(projectSettings, applicationName, workspaceStorage);
    }

    @Override
    public String loadProjectStateRaw() throws StorageException {
        return FsProjectStorage.loadProjectString(applicationName, workspaceStorage);
    }


}

package org.restcomm.connect.rvd.storage;

import org.restcomm.connect.rvd.model.CallControlInfo;
import org.restcomm.connect.rvd.model.ProjectParameters;
import org.restcomm.connect.rvd.model.ProjectSettings;
import org.restcomm.connect.rvd.model.client.WavItem;
import org.restcomm.connect.rvd.model.project.Node;
import org.restcomm.connect.rvd.model.project.ProjectState;
import org.restcomm.connect.rvd.model.server.ProjectIndex;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An in-memory implementation of ProjectDao to be used for testing mainly
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class MemoryProjectDao implements ProjectDao {

    public Map<String, ProjectParameters> projectParameters = new HashMap<String, ProjectParameters>();

    @Override
    public ProjectState loadProject(String applicationId) throws StorageException {
        return null;
    }

    @Override
    public ProjectIndex loadProjectOptions(String applicationId) throws StorageException {
        return null;
    }

    @Override
    public Node loadNode(String moduleName, String applicationId) throws StorageException {
        return null;
    }

    @Override
    public String loadBootstrapInfo(String applicationId) throws StorageException {
        return null;
    }

    @Override
    public ProjectSettings loadSettings(String applicationId) throws StorageException {
        return null;
    }

    @Override
    public CallControlInfo loadWebTriggerInfo(String applicationId) throws StorageException {
        return null;
    }

    @Override
    public void storeWebTriggerInfo(CallControlInfo webTriggerInfo, String applicationId) throws StorageException {

    }

    @Override
    public void removeWebTriggerInfo(String applicationId) {

    }

    @Override
    public void storeSettings(ProjectSettings projectSettings, String applicationId) throws StorageException {

    }

    @Override
    public String loadProjectStateRaw(String applicationId) throws StorageException {
        return null;
    }

    @Override
    public void createProject(String applicationId, ProjectState projectState) throws StorageException {

    }

    @Override
    public void createProjectFromLocation(String applicationId, String sourcePath, String owner) throws StorageException {

    }

    @Override
    public void createProjectFromTemplate(String applicationId, String templateId, String projectAlias, ProjectTemplateDao templateDao, String owner) throws StorageException {

    }

    @Override
    public List<WavItem> listMedia(String applicationId) throws StorageException {
        return null;
    }

    @Override
    public ProjectParameters loadProjectParameters(String applicationId) throws StorageException {
        return projectParameters.get(applicationId);
    }

    @Override
    public void storeProjectParameters(String applicationId, ProjectParameters parameters) throws StorageException {
        projectParameters.put(applicationId, parameters);
    }
}

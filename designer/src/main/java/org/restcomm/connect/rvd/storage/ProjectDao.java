package org.restcomm.connect.rvd.storage;

import org.restcomm.connect.rvd.model.ProjectSettings;
import org.restcomm.connect.rvd.model.project.Node;
import org.restcomm.connect.rvd.model.project.ProjectState;
import org.restcomm.connect.rvd.model.server.ProjectOptions;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

/**
 * Data operations for a project. While constructing this dao, the referenced project is set.
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public interface ProjectDao {
    /**
     * Returns the underlying project name
     *
     * @return the project name as String. Does not return null or ""
     */
    String getName();

    /**
     * Loads and parses the project state data. Returns the parsed ProjectState object or null if the project is not found.
     *
     * @return ProjectState object or null
     * @throws StorageException
     */
    ProjectState loadProject() throws StorageException;

    ProjectOptions loadProjectOptions() throws StorageException;

    Node loadNode(String moduleName) throws StorageException;

    /**
     * Returns current project's bootstrap information as a JSON string. If it does not exist it returns null.
     *
     * @return a JSON block as a string of null
     * @throws StorageException
     */
    String loadBootstrapInfo() throws StorageException;

    /**
     * Loads and returns project 'settings' asset or null if this is not found.
     *
     * @return a ProjectSettings object or null
     *
     * @throws StorageException on serious storage errors
     */
    ProjectSettings loadSettings() throws StorageException;

    void storeSettings(ProjectSettings projectSettings) throws StorageException;

    String loadProjectStateRaw() throws StorageException;
}

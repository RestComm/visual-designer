package org.restcomm.connect.rvd.storage;

import org.apache.commons.io.FileUtils;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.model.CallControlInfo;
import org.restcomm.connect.rvd.model.ProjectSettings;
import org.restcomm.connect.rvd.model.client.WavItem;
import org.restcomm.connect.rvd.model.project.Node;
import org.restcomm.connect.rvd.model.project.ProjectState;
import org.restcomm.connect.rvd.model.server.ProjectOptions;
import org.restcomm.connect.rvd.storage.exceptions.StorageEntityNotFound;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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
    public CallControlInfo loadWebTriggerInfo(String applicationId) throws StorageException {
        try {
            CallControlInfo webTriggerInfo = workspaceStorage.loadEntity("cc", applicationId, CallControlInfo.class);
            return webTriggerInfo;
        } catch (StorageEntityNotFound e) {
            return null;
        }
    }

    @Override
    public void storeWebTriggerInfo(CallControlInfo webTriggerInfo, String applicationId) throws StorageException {
        workspaceStorage.storeEntity(webTriggerInfo, CallControlInfo.class, "cc", applicationId);
    }

    @Override
    public void storeSettings(ProjectSettings projectSettings, String applicationId) throws StorageException {
        FsProjectStorage.storeProjectSettings(projectSettings, applicationId, workspaceStorage);
    }

    @Override
    public String loadProjectStateRaw(String applicationId) throws StorageException {
        return FsProjectStorage.loadProjectString(applicationId, workspaceStorage);
    }

    @Override
    public void createProject(String applicationId, ProjectState projectState) throws StorageException {
        FsProjectStorage.createProjectSlot(applicationId, workspaceStorage);
        FsProjectStorage.storeProject(true, projectState, applicationId, workspaceStorage);
    }

    /**
     * Creates a copy of the project at location pointed to by 'sourcePath' using 'applicationId' as
     * identifier.
     *
     * Note, source path points at the project directory itself
     *
     * @param applicationId
     * @param sourcePath
     */
    @Override
    public void createProjectFromLocation(String applicationId, String sourcePath) throws StorageException {
        // create a directory in the filesystem to host the new project
        FsProjectStorage.createProjectSlot(applicationId, workspaceStorage);
        // create state and project structure
        ProjectState projectState = workspaceStorage.loadEntity("state", sourcePath, ProjectState.class);
        FsProjectStorage.storeProject(true, projectState, applicationId, workspaceStorage);
        // copy project settings
        try {
            ProjectSettings settings = workspaceStorage.loadEntity("settings", sourcePath, ProjectSettings.class);
            storeSettings(settings, applicationId);
        } catch (StorageEntityNotFound e) {
            // do nothing if the settings are not found
        }
        // copy web-trigger information
        try {
            CallControlInfo webTriggerInfo = workspaceStorage.loadEntity("cc", sourcePath, CallControlInfo.class);
            workspaceStorage.storeEntity(webTriggerInfo, CallControlInfo.class, "cc", applicationId);
        } catch (StorageEntityNotFound e) {
            // do nothing if webTrigger info is not there
        }
        // copy .wav/media resources
        List<WavItem> wavs = listMedia(new File(sourcePath));
        for (WavItem wav: wavs) {
            String sourceWavPath = sourcePath + File.separator + RvdConfiguration.WAVS_DIRECTORY_NAME;
            addRawResource(applicationId, RvdConfiguration.WAVS_DIRECTORY_NAME, sourceWavPath, wav.getFilename());
        }
    }

    /**
     * Adds a resource (file) to a project
     *
     * @param applicationId
     * @param relativePath path inside the project to add the resource under. For example "wavs". No forward/trailing slashes should be inscluded. TODO Use symbolic names at some point to describe the type of the resource instead of the relative path like WAV_RESOURCE
     * @param resourcePath absolute path (parent dir) to the resource that will be copied
     * @param resourceName the filename of the resource (includes file extension like .wav)
     * @throws StorageException
     */
    void addRawResource(String applicationId, String relativePath, String resourcePath, String resourceName) throws StorageException {
        String destinationFilePath = workspaceStorage.rootPath + File.separator + applicationId + File.separator + relativePath +  File.separator + resourceName;
        String sourceFilePath = resourcePath + File.separator + resourceName;
        try {
            FileUtils.copyFile(new File(sourceFilePath), new File(destinationFilePath));
        } catch (IOException e) {
            throw new StorageException("Error copying resource " + destinationFilePath + " in project " + applicationId);
        }
    }

    @Override
    public List<WavItem> listMedia(String applicationId) throws StorageException {
        File projectDir = new File(workspaceStorage.rootPath + File.separator + applicationId);
        return listMedia(projectDir);
    }

    /**
     * Generates a list of media files for a project at specific path
     *
     * TODO change WavItem to MediaItem
     *
     * @param projectPath absolute path of the project
     * @return
     * @throws StorageException
     */
    List<WavItem> listMedia(File projectPath) throws StorageException {
        List<WavItem> items = new ArrayList<WavItem>();

        File wavsDir = new File(projectPath.getPath() + File.separator + RvdConfiguration.WAVS_DIRECTORY_NAME);
        if (wavsDir.exists()) {

            File[] entries = wavsDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File anyfile) {
                    if (anyfile.isFile())
                        return true;
                    return false;
                }
            });
            Arrays.sort(entries, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified()) ;
                }
            });

            for (File entry : entries) {
                WavItem item = new WavItem();
                item.setFilename(entry.getName());
                items.add(item);
            }
        }

        return items;
    }
}

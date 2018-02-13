package org.restcomm.connect.rvd.storage;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.exceptions.ProjectDoesNotExist;
import org.restcomm.connect.rvd.exceptions.StreamDoesNotFitInFile;
import org.restcomm.connect.rvd.logging.system.LoggingHelper;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.CallControlInfo;
import org.restcomm.connect.rvd.model.ProjectParameters;
import org.restcomm.connect.rvd.model.ProjectSettings;
import org.restcomm.connect.rvd.model.client.WavItem;
import org.restcomm.connect.rvd.model.project.Node;
import org.restcomm.connect.rvd.model.project.ProjectState;
import org.restcomm.connect.rvd.model.server.ProjectIndex;
import org.restcomm.connect.rvd.storage.exceptions.ProjectAlreadyExists;
import org.restcomm.connect.rvd.storage.exceptions.StorageEntityNotFound;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.storage.exceptions.WavItemDoesNotExist;
import org.restcomm.connect.rvd.utils.RvdUtils;
import org.restcomm.connect.rvd.utils.Zipper;
import org.restcomm.connect.rvd.utils.exceptions.ZipperException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class FsProjectDao implements ProjectDao {

    static Logger logger = RvdLoggers.local;

    JsonModelStorage storage;

    public FsProjectDao(JsonModelStorage workspaceStorage) {
//        if (RvdUtils.isEmpty(applicationName)) {
//            throw new IllegalStateException("Application name is null. Cannot create FsProjectDao");
//        }
//        this.applicationName = applicationName;
        this.storage = workspaceStorage;
    }

    @Override
    public boolean projectExists(String applicationId) {
        return storage.entityExists(applicationId, "state");
    }

    @Override
    public ProjectState loadProject(String applicationId) throws StorageException {
        try {
            return storage.loadEntity("state", applicationId, ProjectState.class);
        } catch (StorageEntityNotFound e) {
            return null;
        }
    }

    @Override
    public ProjectIndex loadProjectOptions(String applicationId) throws StorageException {
        try {
            return storage.loadEntity("project", applicationId+"/data", ProjectIndex.class);
        } catch (StorageEntityNotFound e) {
            return null;
        }
    }

    @Override
    public void storeProjectOptions(String applicationId, ProjectIndex projectOptions) throws StorageException {
        storage.storeEntity(projectOptions, ProjectIndex.class, "project", applicationId+"/data");
    }



    @Override
    public Node loadNode(String moduleName, String applicationId) throws StorageException {
        return storage.loadEntity(moduleName+".mod", applicationId + "/data", Node.class);
    }

    @Override
    public void storeNode(String applicationId, Node node) throws StorageException {
        storage.storeEntity(node, node.getName()+".mod", applicationId+"/data");
    }

    @Override
    public String loadBootstrapInfo(String applicationId) throws StorageException {
        try {
            return storage.loadEntityString("bootstrap", applicationId);
        } catch (StorageEntityNotFound e) {
            return null;
        }
    }

    @Override
    public ProjectSettings loadSettings(String applicationId) throws StorageException {
        try {
            return storage.loadEntity("settings", applicationId, ProjectSettings.class);
        }   catch (StorageEntityNotFound e) {
            return null;
        }
    }

    @Override
    public CallControlInfo loadWebTriggerInfo(String applicationId) throws StorageException {
        try {
            CallControlInfo webTriggerInfo = storage.loadEntity("cc", applicationId, CallControlInfo.class);
            return webTriggerInfo;
        } catch (StorageEntityNotFound e) {
            return null;
        }
    }

    @Override
    public void storeWebTriggerInfo(CallControlInfo webTriggerInfo, String applicationId) throws StorageException {
        storage.storeEntity(webTriggerInfo, CallControlInfo.class, "cc", applicationId);
    }

    @Override
    public void removeWebTriggerInfo(String applicationId) {
        storage.removeEntity("cc", applicationId);
    }

    @Override
    public void storeSettings(ProjectSettings projectSettings, String applicationId) throws StorageException {
        storage.storeEntity(projectSettings, "settings", applicationId);
    }

    @Override
    public String loadProjectStateRaw(String applicationId) throws StorageException {
        return storage.loadEntityString("state", applicationId);
    }

    /**
     * Creates a new project identified by applicationId.
     *
     * It will create default directories assumed existent in subsequent project operations.
     *
     * @param applicationId
     * @param projectState
     * @throws StorageException in case such a project is already in place i.e. the state file already exists
     */
    @Override
    public void createProject(String applicationId, ProjectState projectState) throws StorageException {
        // create the directory hosting the project but oversee any errors in case it already exists (false return value)
        // we use 'state' file to ensure the project is not overwritten
        File newProjectDir = new File(storage.resolveWorkspacePath(applicationId));
        newProjectDir.mkdir();

        File newStateFile = new File(storage.resolveWorkspacePath(applicationId + File.separator + "state" ));
        // we attempt to create a blank state file in an atomic way
        try {
            if ( ! newStateFile.createNewFile() ) {
                // if already exists throw proper error
                throw new ProjectAlreadyExists("Project '" + applicationId + "' already exists");
            }
        } catch (IOException e) {
            throw new StorageException("Error while trying to create state file '" + newStateFile.toString() + "' in an atomic way", e);
        }
        // populate the state file accordingly
        storage.storeEntity(projectState, "state", applicationId);
        // This project has just been created. Let's build related directories
        if ("voice".equals(projectState.getHeader().getProjectKind()) ) {
            String wavsPath = storage.resolveWorkspacePath(applicationId + File.separator + "wavs");
            File wavsDir = new File(  wavsPath );
            wavsDir.mkdir();
        }
    }

    /**
     * Creates a copy of the project at location pointed to by 'sourcePath' using 'applicationId' as
     * identifier.
     *
     * Note, source path points at the project directory itself
     *  @param applicationId
     * @param sourcePath
     * @param owner
     */
    @Override
    public void createProjectFromLocation(String applicationId, String sourcePath, String owner) throws StorageException {
        // load state from remote project (note, sourcePath is absolute)
        ProjectState projectState = storage.loadEntity("state", sourcePath, ProjectState.class);
        if (owner != null) {
            projectState.getHeader().setOwner(owner);
        }
        // create the project skeleton and state file
        createProject(applicationId,projectState);
        // copy project settings
        try {
            ProjectSettings settings = storage.loadEntity("settings", sourcePath, ProjectSettings.class);
            storeSettings(settings, applicationId);
        } catch (StorageEntityNotFound e) {
            // do nothing if the settings are not found
        }
        // copy web-trigger information
        try {
            CallControlInfo webTriggerInfo = storage.loadEntity("cc", sourcePath, CallControlInfo.class);
            storage.storeEntity(webTriggerInfo, CallControlInfo.class, "cc", applicationId);
        } catch (StorageEntityNotFound e) {
            // do nothing if webTrigger info is not there
        }
        // copy parameters
        try {
            ProjectParameters parameters = storage.loadEntity("parameters", sourcePath, ProjectParameters.class);
            storeProjectParameters(applicationId, parameters);
        } catch (StorageEntityNotFound e) {
            // do nothing
        }

        // copy .wav/media resources
        List<WavItem> wavs = listMedia(new File(sourcePath + File.separator + RvdConfiguration.WAVS_DIRECTORY_NAME));
        for (WavItem wav: wavs) {
            String sourceWavPath = sourcePath + File.separator + RvdConfiguration.WAVS_DIRECTORY_NAME;
            addRawResource(applicationId, RvdConfiguration.WAVS_DIRECTORY_NAME, sourceWavPath, wav.getFilename());
        }
    }

    @Override
    public void createProjectFromTemplate(String applicationId, String templateId, String projectAlias, ProjectTemplateDao templateDao, String owner) throws StorageException {
        String sourceProjectPath = ((FsProjectTemplateDao)templateDao).resolveTemplateProjectPath(templateId, projectAlias);
        createProjectFromLocation(applicationId, sourceProjectPath, owner );
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
        String destinationFilePath = storage.resolveWorkspacePath(applicationId + File.separator + relativePath +  File.separator + resourceName);
        String sourceFilePath = resourcePath + File.separator + resourceName;
        try {
            FileUtils.copyFile(new File(sourceFilePath), new File(destinationFilePath));
        } catch (IOException e) {
            throw new StorageException("Error copying resource " + destinationFilePath + " in project " + applicationId);
        }
    }

    @Override
    public InputStream getMediaAsStream(String projectName, String filename) throws StorageException {
        try {
            return storage.loadStream(RvdConfiguration.WAVS_DIRECTORY_NAME + File.separator + filename, projectName);
        } catch (StorageEntityNotFound e) {
            throw new WavItemDoesNotExist("Wav file does not exist - " + filename, e);
        }
    }

    /**
     * Generates a list of media files for a project
     *
     * TODO change WavItem to MediaItem
     *
     * @return
     * @throws StorageException
     */
    @Override
    public List<WavItem> listMedia(String applicationId) throws StorageException {
        List<WavItem> items = new ArrayList<>();
        File wavsDir = new File(storage.resolveWorkspacePath( applicationId + File.separator + RvdConfiguration.WAVS_DIRECTORY_NAME));
        return listMedia(wavsDir);
    }

    public List<WavItem> listMedia(File wavsDir) throws StorageException {
        List<WavItem> items = new ArrayList<>();
        if (wavsDir.exists()) {
            List<String> filenames = storage.listContents(wavsDir.getPath(), ".*", false);
            for (String filename: filenames) {
                WavItem item = new WavItem();
                item.setFilename(filename);
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public void storeMediaFromStream(String projectName, String wavname, InputStream wavStream, Integer maxSize) throws StorageException, StreamDoesNotFitInFile {
        String wavPathname = storage.resolveWorkspacePath(projectName + File.separator +  RvdConfiguration.WAVS_DIRECTORY_NAME + File.separator + wavname);
        if(logger.isDebugEnabled())
            logger.log(Level.DEBUG, LoggingHelper.buildMessage(FsProjectDao.class,"storeWav", "writing wav file to {0}", wavPathname));
        try {
            RvdUtils.streamToFile(wavStream, new File(wavPathname), maxSize);
        } catch (IOException e) {
            throw new StorageException("Error writing to " + wavPathname, e);
        }
    }

    @Override
    public void removeMedia(String applicationId, String mediaName) throws WavItemDoesNotExist {
        String filepath = storage.resolveWorkspacePath(applicationId + File.separator +  RvdConfiguration.WAVS_DIRECTORY_NAME + File.separator + mediaName);
        File wavfile = new File(filepath);
        if ( wavfile.delete() ) {
            if(logger.isDebugEnabled())
                logger.log(Level.DEBUG, LoggingHelper.buildMessage(FsProjectDao.class,"deleteWav","deleted {0} from {1} app", new Object[] {mediaName, applicationId}));
        }
        else {
            //logger.warn( "Cannot delete " + wavname + " from " + projectName + " app" );
            throw new WavItemDoesNotExist("Wav file does not exist - " + filepath );
        }
    }

    @Override
    public ProjectParameters loadProjectParameters(String applicationId) throws StorageException {
        try {
            ProjectParameters parameters = storage.loadEntity("parameters", applicationId, ProjectParameters.class);
            return parameters;
        } catch (StorageEntityNotFound e) {
            return  null;
        }
    }

    @Override
    public void storeProjectParameters(String applicationId, ProjectParameters parameters) throws StorageException {
        storage.storeEntity(parameters, ProjectParameters.class, "parameters", applicationId);
    }

    @Override
    public void removeProject(String applicationId) throws ProjectDoesNotExist, StorageException {
        try {
            File projectDir = new File( storage.resolveWorkspacePath(applicationId));
            FileUtils.deleteDirectory(projectDir);
        } catch (IOException e) {
            throw new StorageException("Error removing directory '" + applicationId + "'", e);
        }
    }

    @Override
    public void updateProjectState(String applicationId, ProjectState state) throws StorageException {
        storage.storeEntity(state, "state", applicationId);
    }


    @Override
    public InputStream archiveProject(String projectName) throws StorageException {
        String path = storage.resolveWorkspacePath(projectName);
        File tempFile;
        try {
            tempFile = File.createTempFile("RVDprojectArchive",".zip");
        } catch (IOException e1) {
            throw new StorageException("Error creating temp file for archiving project " + projectName, e1);
        }

        InputStream archiveStream;
        try {
            Zipper zipper = new Zipper(tempFile);
            zipper.addDirectoryRecursively(path, false);
            zipper.finish();

            // open a stream on this file
            archiveStream = new FileInputStream(tempFile);
            return archiveStream;
        } catch (ZipperException e) {
            throw new StorageException( "Error archiving " + projectName, e);
        } catch (FileNotFoundException e) {
            throw new StorageException("This is weird. Can't find the temp file i just created for archiving project " + projectName, e);
        } finally {
            // Always delete the file. The underlying file content still exists because the archiveStream refers to it (for Linux only). It will be deleted when the stream is closed
            tempFile.delete();
        }
    }

}

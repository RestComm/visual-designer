/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2016, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package org.restcomm.connect.rvd.storage;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.google.gson.JsonSyntaxException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.exceptions.StreamDoesNotFitInFile;
import org.restcomm.connect.rvd.RvdContext;
import org.restcomm.connect.rvd.logging.system.LoggingHelper;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.project.Node;
import org.restcomm.connect.rvd.model.project.ProjectState;
import org.restcomm.connect.rvd.model.project.StateHeader;
import org.restcomm.connect.rvd.model.client.WavItem;
import org.restcomm.connect.rvd.model.server.ProjectIndex;
import org.restcomm.connect.rvd.storage.exceptions.BadProjectHeader;
import org.restcomm.connect.rvd.storage.exceptions.ProjectAlreadyExists;
import org.restcomm.connect.rvd.storage.exceptions.StorageEntityNotFound;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.storage.exceptions.WavItemDoesNotExist;
import org.restcomm.connect.rvd.utils.RvdUtils;
import org.restcomm.connect.rvd.utils.Zipper;
import org.restcomm.connect.rvd.utils.exceptions.ZipperException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.restcomm.connect.rvd.model.ProjectSettings;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class FsProjectStorage {
    static Logger logger = RvdLoggers.local;

    public static InputStream getWav(String projectName, String filename, WorkspaceStorage workspaceStorage) throws StorageException {
        try {
            return workspaceStorage.loadStream(RvdConfiguration.WAVS_DIRECTORY_NAME + File.separator + filename, projectName);
        } catch (StorageEntityNotFound e) {
            throw new WavItemDoesNotExist("Wav file does not exist - " + filename, e);
        }
    }

    public static String loadBootstrapInfo(String projectName, WorkspaceStorage workspaceStorage) throws StorageException {
        return workspaceStorage.loadEntityString("bootstrap", projectName);
    }


    public static ProjectIndex loadProjectOptions(String projectName, WorkspaceStorage workspaceStorage) throws StorageException {
        ProjectIndex projectOptions = workspaceStorage.loadEntity("project", projectName+"/data", ProjectIndex.class);
        return projectOptions;
    }

    public static void storeProjectOptions(ProjectIndex projectOptions, String projectName, WorkspaceStorage workspaceStorage) throws StorageException {
        workspaceStorage.storeEntity(projectOptions, ProjectIndex.class, "project", projectName+"/data");
    }

    /**
     * Stores a full-fledged rvd module (.mod file) to the filesystem. Note that this is different from older practice
     * of storing only the step names in a .node file that was done by storeNodeStepnames().
     *
     * @param node
     * @param projectName
     * @param storage
     * @throws StorageException
     */
    public static void storeNode(Node node, String projectName, WorkspaceStorage storage) throws StorageException {
        storage.storeEntity(node, node.getName()+".mod", projectName+"/data");
    }

    /**
     * Loads a full-fledged rvd module (.mod file) from the filesystem. ote that this is different from older practice
     * of storing only the step names in a .node file that was done by loadNodeStepnames().
     *
     * @param projectName
     * @param nodeName
     * @param storage
     * @throws StorageException
     */
    public static Node loadNode(String projectName, String nodeName, WorkspaceStorage storage ) throws StorageException {
        return storage.loadEntity(nodeName+".mod", projectName + "/data", Node.class);
    }

    public static ProjectSettings loadProjectSettings(String projectName, WorkspaceStorage storage) throws StorageException {
        return storage.loadEntity("settings", projectName, ProjectSettings.class);
    }

    public static void storeProjectSettings(ProjectSettings projectSettings, String projectName, WorkspaceStorage storage) throws StorageException {
        storage.storeEntity(projectSettings, "settings", projectName);
    }

    public static ProjectState loadProject(String projectName, WorkspaceStorage storage) throws StorageException {
        return storage.loadEntity("state", projectName, ProjectState.class);
    }

    public static String loadProjectString(String projectName, WorkspaceStorage storage) throws StorageException {
        return storage.loadEntityString("state", projectName);
    }

    private static void buildDirStructure(ProjectState state, String name, WorkspaceStorage storage) {
        if ("voice".equals(state.getHeader().getProjectKind()) ) {
            File wavsDir = new File(  storage.rootPath + "/" + name + "/" + "wavs" );
            wavsDir.mkdir();
        }
    }

    public static void storeProject(boolean firstTime, ProjectState state, String projectName, WorkspaceStorage storage) throws StorageException {
        storage.storeEntity(state, "state", projectName);
        if (firstTime)
            buildDirStructure(state, projectName, storage);

    }

    public static StateHeader loadStateHeader(String projectName, WorkspaceStorage storage) throws StorageException {
        String stateData = storage.loadEntityString("state", projectName);
        JsonParser parser = new JsonParser();
        JsonElement header_element = null;
        try {
            header_element = parser.parse(stateData).getAsJsonObject().get("header");
        } catch (JsonSyntaxException e) {
            throw new StorageException("Error loading header for project '" + projectName +"'",e);
        }
        if ( header_element == null )
            throw new BadProjectHeader("No header found. This is probably an old project");

        Gson gson = new Gson();
        StateHeader header = gson.fromJson(header_element, StateHeader.class);

        return header;
    }

    public static boolean projectExists(String projectName, WorkspaceStorage workspaceStorage) {
        return workspaceStorage.entityExists(projectName, "");
    }

    public static void createProjectSlot(String projectName, WorkspaceStorage storage) throws StorageException {
        if ( projectExists(projectName, storage) )
            throw new ProjectAlreadyExists("Project '" + projectName + "' already exists");

        //String projectPath = storageBase.getWorkspaceBasePath()  +  File.separator + projectName;
        String projectPath = storage.rootPath  +  File.separator + projectName;
        File projectDirectory = new File(projectPath);
        if ( !projectDirectory.mkdir() )
            throw new StorageException("Cannot create project directory. Don't know why - " + projectDirectory );

    }

    public static void deleteProject(String projectName, WorkspaceStorage storage) throws StorageException {
        try {
            File projectDir = new File(storage.rootPath  + File.separator + projectName);
            FileUtils.deleteDirectory(projectDir);
        } catch (IOException e) {
            throw new StorageException("Error removing directory '" + projectName + "'", e);
        }
    }

    public static InputStream archiveProject(String projectName, WorkspaceStorage storage) throws StorageException {
        String path = storage.rootPath + File.separator + projectName; //storageBase.getProjectBasePath(projectName);
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

    /**
     * Returns an non-existing project name based on the given one. Ideally it returns the same name. If null or blank
     * project name given the 'Untitled' name is tried.
     * @throws StorageException in case the first 50 project names tried are already occupied
     */
    public static String getAvailableProjectName(String projectName, WorkspaceStorage storage) throws StorageException {
        if ( projectName == null || "".equals(projectName) )
            projectName = "Unititled";

        String baseProjectName = projectName;
        int counter = 1;
        while (true && counter < 50) { // try up to 50 times, no more
            if ( ! projectExists(projectName,storage) )
                return projectName;
            projectName = baseProjectName + " " +  counter;
            counter ++;
        }

        throw new StorageException("Can't find an available project name for base name '" + projectName + "'");
    }

    public static void importProjectFromDirectory(File sourceProjectDirectory, String projectName, boolean overwrite, WorkspaceStorage storage) throws StorageException {
        try {
            createProjectSlot(projectName, storage);
        } catch (ProjectAlreadyExists e) {
            if ( !overwrite )
                throw e;
            else {
                File destProjectDirectory = new File(storage.rootPath + File.separator + projectName);
                try {
                    FileUtils.cleanDirectory(destProjectDirectory);
                    FileUtils.copyDirectory(sourceProjectDirectory, destProjectDirectory);
                } catch (IOException e1) {
                    throw new StorageException("Error importing project '" + projectName + "' from directory: " + sourceProjectDirectory);
                }
            }
        }
    }

    private static String getProjectBasePath(String projectName, WorkspaceStorage storage) {
        return storage.rootPath + File.separator + projectName;
    }

    private static String getProjectWavsPath( String projectName, WorkspaceStorage storage ) {
        return getProjectBasePath(projectName,storage) + File.separator + RvdConfiguration.WAVS_DIRECTORY_NAME;
    }

    public static void storeWav(String projectName, String wavname, InputStream wavStream, WorkspaceStorage storage, Integer maxSize) throws StorageException, StreamDoesNotFitInFile {
        String wavPathname = getProjectWavsPath(projectName, storage) + File.separator + wavname;
        if(logger.isDebugEnabled())
            logger.log(Level.DEBUG, LoggingHelper.buildMessage(FsProjectStorage.class,"storeWav", "writing wav file to {0}", wavPathname));
        try {
            RvdUtils.streamToFile(wavStream, new File(wavPathname), maxSize);
        } catch (IOException e) {
            throw new StorageException("Error writing to " + wavPathname, e);
        }
    }

    public static List<WavItem> listWavs(String projectName, WorkspaceStorage storage) throws StorageException {
        List<WavItem> items = new ArrayList<WavItem>();

        //File workspaceDir = new File(workspaceBasePath + File.separator + appName + File.separator + "wavs");
        File wavsDir = new File(getProjectWavsPath(projectName,storage));
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
        }// else
            //throw new BadWorkspaceDirectoryStructure();

        return items;
    }

    /**
     * Returns a WavItem list for all .wav files insude the /audio RVD directory. No project is involved here.
     * @param rvdContext
     * @return
     */
    public static List<WavItem> listBundledWavs( RvdContext rvdContext ) {
        List<WavItem> items = new ArrayList<WavItem>();

        String contextRealPath = RvdUtils.addTrailingSlashIfMissing(rvdContext.getServletContext().getRealPath("/"));
        String audioRealPath = contextRealPath + "audio";
        String contextPath = rvdContext.getServletContext().getContextPath();

        File dir = new File(audioRealPath);
        Collection<File> audioFiles = FileUtils.listFiles(dir, new SuffixFileFilter(".wav"), TrueFileFilter.INSTANCE );
        for (File anyFile: audioFiles) {
            WavItem item = new WavItem();
            String itemRelativePath = anyFile.getPath().substring(contextRealPath.length());
            String presentationName = anyFile.getPath().substring(contextRealPath.length() + "audio".length() );
            item.setUrl( contextPath + "/" + itemRelativePath );
            item.setFilename(presentationName);
            items.add(item);
        }
        return items;
    }

    public static void deleteWav(String projectName, String wavname, WorkspaceStorage storage) throws WavItemDoesNotExist {
        String filepath = getProjectWavsPath(projectName, storage) + File.separator + wavname;
        File wavfile = new File(filepath);
        if ( wavfile.delete() ) {
            if(logger.isDebugEnabled())
                logger.log(Level.DEBUG, LoggingHelper.buildMessage(FsProjectStorage.class,"deleteWav","deleted {0} from {1} app", new Object[] {wavname, projectName}));
        }
        else {
            //logger.warn( "Cannot delete " + wavname + " from " + projectName + " app" );
            throw new WavItemDoesNotExist("Wav file does not exist - " + filepath );
        }

    }

    public static void backupProjectState(String projectName, WorkspaceStorage storage) throws StorageException {
        File sourceStateFile = new File(storage.rootPath + File.separator + projectName + File.separator + "state");
        File backupStateFile = new File(storage.rootPath + File.separator + projectName + File.separator + "state" + ".old");

        try {
            FileUtils.copyFile(sourceStateFile, backupStateFile);
        } catch (IOException e) {
            throw new StorageException("Error creating state file backup: " + backupStateFile);
        }
    }

    public static void updateProjectState(String projectName, String newState, WorkspaceStorage storage) throws StorageException {
        FileOutputStream stateFile_os;
        try {
            stateFile_os = new FileOutputStream(storage.rootPath + File.separator + projectName + File.separator + "state");
            IOUtils.write(newState, stateFile_os, Charset.forName("UTF-8"));
            stateFile_os.close();
        } catch (FileNotFoundException e) {
            throw new StorageException("Error updating state file for project '" + projectName + "'", e);
        } catch (IOException e) {
            throw new StorageException("Error updating state file for project '" + projectName + "'", e);
        }
    }

}



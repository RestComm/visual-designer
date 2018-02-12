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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.JsonSyntaxException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.restcomm.connect.rvd.RvdContext;
import org.restcomm.connect.rvd.model.project.ProjectState;
import org.restcomm.connect.rvd.model.project.StateHeader;
import org.restcomm.connect.rvd.model.client.WavItem;
import org.restcomm.connect.rvd.storage.exceptions.BadProjectHeader;
import org.restcomm.connect.rvd.storage.exceptions.ProjectAlreadyExists;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.utils.RvdUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.restcomm.connect.rvd.model.ProjectSettings;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class FsProjectStorage {

    public static String loadBootstrapInfo(String projectName, OldWorkspaceStorage oldWorkspaceStorage) throws StorageException {
        return oldWorkspaceStorage.loadEntityString("bootstrap", projectName);
    }

    public static ProjectSettings loadProjectSettings(String projectName, OldWorkspaceStorage storage) throws StorageException {
        return storage.loadEntity("settings", projectName, ProjectSettings.class);
    }

    public static void storeProjectSettings(ProjectSettings projectSettings, String projectName, OldWorkspaceStorage storage) throws StorageException {
        storage.storeEntity(projectSettings, "settings", projectName);
    }

    public static ProjectState loadProject(String projectName, OldWorkspaceStorage storage) throws StorageException {
        return storage.loadEntity("state", projectName, ProjectState.class);
    }

    public static String loadProjectString(String projectName, OldWorkspaceStorage storage) throws StorageException {
        return storage.loadEntityString("state", projectName);
    }

    private static void buildDirStructure(ProjectState state, String name, OldWorkspaceStorage storage) {
        if ("voice".equals(state.getHeader().getProjectKind()) ) {
            File wavsDir = new File(  storage.rootPath + "/" + name + "/" + "wavs" );
            wavsDir.mkdir();
        }
    }

    public static void storeProject(boolean firstTime, ProjectState state, String projectName, OldWorkspaceStorage storage) throws StorageException {
        storage.storeEntity(state, "state", projectName);
        if (firstTime)
            buildDirStructure(state, projectName, storage);
    }

    public static StateHeader loadStateHeader(String projectName, OldWorkspaceStorage storage) throws StorageException {
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

    public static boolean projectExists(String projectName, OldWorkspaceStorage oldWorkspaceStorage) {
        return oldWorkspaceStorage.entityExists(projectName, "");
    }

    public static void createProjectSlot(String projectName, OldWorkspaceStorage storage) throws StorageException {
        if ( projectExists(projectName, storage) )
            throw new ProjectAlreadyExists("Project '" + projectName + "' already exists");

        //String projectPath = storageBase.getWorkspaceBasePath()  +  File.separator + projectName;
        String projectPath = storage.rootPath  +  File.separator + projectName;
        File projectDirectory = new File(projectPath);
        if ( !projectDirectory.mkdir() )
            throw new StorageException("Cannot create project directory. Don't know why - " + projectDirectory );

    }

    public static void deleteProject(String projectName, OldWorkspaceStorage storage) throws StorageException {
        try {
            File projectDir = new File(storage.rootPath  + File.separator + projectName);
            FileUtils.deleteDirectory(projectDir);
        } catch (IOException e) {
            throw new StorageException("Error removing directory '" + projectName + "'", e);
        }
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

    public static void backupProjectState(String projectName, OldWorkspaceStorage storage) throws StorageException {
        File sourceStateFile = new File(storage.rootPath + File.separator + projectName + File.separator + "state");
        File backupStateFile = new File(storage.rootPath + File.separator + projectName + File.separator + "state" + ".old");

        try {
            FileUtils.copyFile(sourceStateFile, backupStateFile);
        } catch (IOException e) {
            throw new StorageException("Error creating state file backup: " + backupStateFile);
        }
    }

    public static void updateProjectState(String projectName, String newState, OldWorkspaceStorage storage) throws StorageException {
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



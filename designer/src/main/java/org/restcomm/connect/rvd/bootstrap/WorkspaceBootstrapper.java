package org.restcomm.connect.rvd.bootstrap;

import org.apache.commons.io.FileUtils;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.exceptions.BootstrappingException;

import java.io.File;
import java.io.IOException;

/**
 * A class to encapsulate all startup tasks for workspace initialization for example create the @users directory
 * if it does not exist.
 *
 * @author Orestis Tsakiridis
 */
public class WorkspaceBootstrapper {

    private String rootLocation; // the root directory of the workspace that contains project directories as children
    private String templatesPath;

    public WorkspaceBootstrapper(String rootLocation, String templatesLocation) {
        this.rootLocation = rootLocation;
        this.templatesPath = templatesLocation;
        initWorkspace();
    }

    public WorkspaceBootstrapper(RvdConfiguration configuration) throws BootstrappingException {
        this.rootLocation = configuration.getWorkspaceBasePath();
        this.templatesPath = configuration.getProjectTemplatesWorkspacePath();
        initWorkspace();
    }

    private void initWorkspace() {
        File rootDir = new File(rootLocation);
        if (!rootDir.exists() || !rootDir.isDirectory() ) {
            String message = "Error bootstrapping RVD workspace at '" + rootLocation + "'. Location does not exist or is not a directory.";
            throw new RuntimeException(message);
        }
    }

    /**
     * Executes all operations for workspace bootstrapping
     */
    public void run() throws BootstrappingException {
        createDirectories();
    }

    /**
     * Creates users directory inside the workspace. That's where user-specific information is stored
     */
    void createDirectories() throws BootstrappingException {
        String dirName = rootLocation + "/" + RvdConfiguration.USERS_DIRECTORY_NAME;
        File usersDir = new File(dirName);
        if (!usersDir.exists()) {
            usersDir.mkdir();
        }
        File templatesDir = new File(templatesPath);
        if (!templatesDir.exists()) {
            try {
                FileUtils.forceMkdir(templatesDir);
            } catch (IOException e) {
                throw new BootstrappingException("Could not create templates directory: " + templatesPath, e);
            }
        }
    }
}

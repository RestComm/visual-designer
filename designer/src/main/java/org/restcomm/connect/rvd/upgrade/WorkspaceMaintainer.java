package org.restcomm.connect.rvd.upgrade;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.logging.system.LoggingHelper;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.WorkspaceStatus;
import org.restcomm.connect.rvd.storage.JsonModelStorage;
import org.restcomm.connect.rvd.storage.WorkspaceDao;
import org.restcomm.connect.rvd.storage.exceptions.StorageEntityNotFound;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.utils.RvdUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class that helps maintain and upgrade a workspace
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class WorkspaceMaintainer implements WorkspaceDao {

    static Logger logger = RvdLoggers.local;
    static final String TEMPLATES_RESOURCE_DIR_NAME = "project-templates"; // the name of the sub-dir under webapp where the project templates are stored

    JsonModelStorage storage;
    String fsContextPath;
    Map<Integer, List<TaskType>> taskMapping = new HashMap<Integer, List<TaskType>>();

    enum TaskType {
        COPY_TEMPLATES
    }

    /**
     *
     * @param storage
     * @param fsContextPath The absolute path of webapp like when deployed
     */
    public WorkspaceMaintainer(JsonModelStorage storage, String fsContextPath) {
        this.storage = storage;
        this.fsContextPath = RvdUtils.removeTrailingSlashIfPresent(fsContextPath);
        taskMapping.put(1, Arrays.asList(new TaskType[] {TaskType.COPY_TEMPLATES} )); // for workspace_version 1 we need to copy templates only
    }

    public WorkspaceStatus loadWorkspaceStatus() throws StorageException {
        try {
            return storage.loadEntity("status", "", WorkspaceStatus.class);
        } catch (StorageEntityNotFound e) {
            return null;
        }
    }

    public void storeWorkspaceStatus(WorkspaceStatus workspaceStatus) throws StorageException {
        storage.storeEntity(workspaceStatus, WorkspaceStatus.class, "status", "");
    }

    public void checkWorkspace() throws StorageException {
        RvdLoggers.local.log(Level.INFO, LoggingHelper.buildMessage(getClass(),"checkWorkspace","Starting WorkspaceMaintainer checks..."));
        WorkspaceStatus workspaceStatus = loadWorkspaceStatus();
        if (workspaceStatus == null || workspaceStatus.getVersion() == null) {
            runAllTasks(0,RvdConfiguration.WORKSPACE_VERSION);
            workspaceStatus = new WorkspaceStatus(RvdConfiguration.WORKSPACE_VERSION); // update to current  workspace version
            storeWorkspaceStatus(workspaceStatus);
        } else
        if (workspaceStatus.getVersion() < RvdConfiguration.WORKSPACE_VERSION) {
            runAllTasks(workspaceStatus.getVersion(), RvdConfiguration.WORKSPACE_VERSION);
            workspaceStatus = new WorkspaceStatus(RvdConfiguration.WORKSPACE_VERSION); // update to current  workspace version
            storeWorkspaceStatus(workspaceStatus);
        } else {
            RvdLoggers.local.log(Level.INFO, LoggingHelper.buildMessage(getClass(),"checkWorkspace","No tasks need to be triggered."));
        }
    }

    void runAllTasks(Integer fromVersion, Integer toVersion) {
        Integer version = fromVersion + 1;
        while ( version <= toVersion) {
            List<TaskType> tasks = taskMapping.get(version);
            if (tasks != null) {
                if (tasks.contains(TaskType.COPY_TEMPLATES)) {
                    boolean ranClean = updateTemplatesTask(version);
                    // TODO do something with ranClean
                }
            }
            version ++;
        }
    }

    boolean updateTemplatesTask(Integer version) {
        RvdLoggers.local.log(Level.WARN, LoggingHelper.buildMessage(getClass(),"updateTemplatesTask","Running COPY_TEMPLATES task for toVersion = " + version));
        boolean ranClean = true;
        File templatesParentDir = new File(fsContextPath + File.separator + TEMPLATES_RESOURCE_DIR_NAME);
        if (templatesParentDir.exists()) {
            File[] entries = templatesParentDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File anyfile) {
            return anyfile.isDirectory();
                }
            });

            for (File entry: entries) {
                try {
                    storage.copyDirToWorkspace(templatesParentDir.getPath() + File.separator + entry.getName(), RvdConfiguration.TEMPLATES_DIRECTORY_NAME);
                    RvdLoggers.local.log(Level.INFO, LoggingHelper.buildMessage(getClass(),"updateTemplatesTask","Project template " + entry.getName() + " (re)installed to workspace"));
                } catch (StorageException e) {
                    ranClean = false;
                    RvdLoggers.local.log(Level.ERROR, LoggingHelper.buildMessage(getClass(),"updateTemplatesTask","Error copying project template " + entry.getName()));
                }
            }
        } else {
            RvdLoggers.local.log(Level.WARN, LoggingHelper.buildMessage(getClass(),"updateTemplatesTask","The template resource directory is empty although there is a scheduled copying of templates for this binary "));
        }
        return ranClean;
    }
}

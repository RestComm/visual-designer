/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, Telestax Inc and individual contributors
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
 */

package org.restcomm.connect.rvd.upgrade;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.restcomm.connect.rvd.BuildService;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.exceptions.InvalidProjectVersion;
import org.restcomm.connect.rvd.logging.system.LoggingHelper;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.project.ProjectState;
import org.restcomm.connect.rvd.model.project.StateHeader;
import org.restcomm.connect.rvd.storage.FsProjectDao;
import org.restcomm.connect.rvd.storage.JsonModelStorage;
import org.restcomm.connect.rvd.storage.ProjectDao;
import org.restcomm.connect.rvd.storage.WorkspaceStorage;
import org.restcomm.connect.rvd.storage.exceptions.BadProjectHeader;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.upgrade.exceptions.NoUpgradePathException;
import org.restcomm.connect.rvd.upgrade.exceptions.UpgradeException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class UpgradeService {
    static Logger logger = RvdLoggers.local;

    public enum UpgradabilityStatus {
        SHOULD_UPGRADE, NOT_NEEDED, NOT_SUPPORTED
    }

    // valid project versions. If a version is not here it can either considered 'future' version or garbage.
    public static final String[] versionPath = new String[] {"rvd714","1.0", "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "1.8","1.9", "1.10", "1.11","1.12", "1.13", "1.14", "1.15"};
    // project versions where the project state .json file should be upgraded OR a build triggered
    static final List<String> upgradesPath = Arrays.asList(new String [] {"1.0","1.6","1.13"});

    private JsonModelStorage storage;

    public UpgradeService(JsonModelStorage storage) {
        this.storage = storage;
    }

    /**
     * Checks whether a project should/can be upgraded according to Designer's project version and this project's version.
     * Depending on the result, it returns the following:
     *
     * NOT_SUPPORTED    :   project's version is not known at all
     * SHOULD_UPGRADE       :   the needs to be upgraded
     * NOT_NEEDED       :   the project doesn't need to be upgraded. Binary is compatible with it
     *
     * Note, we don't have a case for known projects that can't be upgraded because they are too old (yet)
     * .
     * @param projectVersion
     * @param rvdProjectVersion
     * @return
     * @throws InvalidProjectVersion
     */
    public static UpgradabilityStatus checkUpgradability(String projectVersion, String rvdProjectVersion) throws InvalidProjectVersion {
        int projectIndex = -1;
        int rvdIndex = -1;
        for ( int i = 0; i < versionPath.length; i ++ ) {
            if (versionPath[i].equals(projectVersion) )
                projectIndex = i;
            if (versionPath[i].equals(rvdProjectVersion))
                rvdIndex = i;
        }
        if (rvdIndex == -1)
            throw new IllegalStateException("RVD project version not found in the versionPath.");
        if (projectIndex == -1)
            return UpgradabilityStatus.NOT_SUPPORTED;

        // ok, we have the version path. Is there any upgrade there ?
        int i = projectIndex + 1;
        boolean upgradesInvolved = false;
        while (i <= rvdIndex) {
            if (upgradesPath.contains(versionPath[i]))
                upgradesInvolved = true;
            i ++;
        }
        if (upgradesInvolved)
            return UpgradabilityStatus.SHOULD_UPGRADE;
        else
            return UpgradabilityStatus.NOT_NEEDED;
    }

    /**
     * Upgrades a project to current RVD supported version
     * @param projectName
     * @return null for projects already upgraded or older supported projects. For projects that were indeed upgraded it returns the root JsonElement
     * @throws StorageException
     * @throws UpgradeException
     */
    public JsonElement upgradeProject(String projectName) throws StorageException, UpgradeException {
        StateHeader header = null;
        String startVersion = null;
        try {
            header = storage.loadStateHeader(projectName);
            startVersion = header.getVersion();
        } catch (BadProjectHeader e) {
            // it looks like this is an old project.
            startVersion = "rvd714"; // assume this is an rvd714 project. It could be 713 as well...
        }

        if ( startVersion.equals(RvdConfiguration.RVD_PROJECT_VERSION) )
            return null;
        if ( checkUpgradability(startVersion, RvdConfiguration.RVD_PROJECT_VERSION) == UpgradabilityStatus.NOT_NEEDED ) {
            // if current binary is compatible with old project no need to batch upgrade
            return null;
        }

        if(RvdLoggers.local.isEnabledFor(Level.INFO)) {
            RvdLoggers.local.log(Level.INFO, LoggingHelper.buildMessage(getClass(),"upgradeProject","upgrading '" + projectName + "' from version " + startVersion ));
        }

        String version = startVersion;
        String source = storage.loadEntityString("state", projectName);
        JsonParser parser = new JsonParser();
        JsonElement root = parser.parse(source);

        for ( int i = 0; i < versionPath.length; i ++ ) {
            if ( versionPath[i].equals(version) ) {
                // we found the version to start the upgrade
                ProjectUpgrader upgrader = ProjectUpgraderFactory.create(version);
                root = upgrader.upgrade(root);
                version = upgrader.getResultingVersion();

                if (version.equals(versionPath[versionPath.length-1] ) )
                    break;

                // if we haven't reached the final version yet keep upgrading
            }
        }

        if ( ! version.equals(versionPath[versionPath.length-1]) ) {
            throw new NoUpgradePathException("No upgrade path for project " + projectName + ". Best effort from version: " + startVersion + " - to version: " + versionPath[versionPath.length-1]);
        }

        backupProjectState(projectName, storage);
        storage.storeEntityString(root.toString(), "state", projectName);
        //FsProjectStorage.updateProjectState(projectName, root.toString(), storage);
        return root;
    }
    /**
     * Upgrades all projects inside the project workspace to the version supported by current RVD
     * @throws StorageException
     */
    public void upgradeWorkspace() throws StorageException {
        ProjectDao projectDao = new FsProjectDao(storage);
        BuildService buildService = new BuildService(projectDao);
        int upgradedCount = 0;
        int uptodateCount = 0;
        int failedCount = 0;

        List<String> projectNames = storage.listContents(".", "[^@].+", true); //FsProjectStorage.listProjectNames(storage);
        for ( String projectName : projectNames ) {
            try {
                if ( upgradeProject(projectName) != null ) {
                    upgradedCount ++;
                    if(RvdLoggers.local.isEnabledFor(Level.INFO)) {
                        RvdLoggers.local.log(Level.INFO, LoggingHelper.buildMessage(getClass(),"upgradeWorkspace", "project '" + projectName + "' upgraded to version " + RvdConfiguration.RVD_PROJECT_VERSION ));
                    }
                    try {
                        ProjectState projectState = projectDao.loadProject(projectName); //FsProjectStorage.loadProject(projectName, storage);
                        if (projectState == null) {
                            RvdLoggers.local.log(Level.WARN, "error building upgraded project '" + projectName + "' : state file not found");
                        } else {
                            buildService.buildProject(projectName, projectState);
                            if (RvdLoggers.local.isEnabledFor(Level.INFO)) {
                                RvdLoggers.local.log(Level.INFO, LoggingHelper.buildMessage(getClass(), "upgradeWorkspace", "project '" + projectName + "' built"));
                            }
                        }
                    } catch (StorageException e) {
                        RvdLoggers.local.log(Level.WARN, "error building upgraded project '" + projectName + "'", e);
                    }
                } else
                    uptodateCount ++;
            } catch (StorageException | UpgradeException e) {
                failedCount ++;
                RvdLoggers.local.log(Level.ERROR, LoggingHelper.buildMessage(getClass(),"upgradeWorkspace","error upgrading project '" + projectName + "' to version " + RvdConfiguration.RVD_PROJECT_VERSION), e );
            }
        }
        if ( failedCount > 0 )
            if(RvdLoggers.local.isEnabledFor(Level.INFO)) {
                RvdLoggers.local.log(Level.INFO, LoggingHelper.buildMessage(getClass(),"upgradeWorkspace", "" + failedCount + " RVD projects failed upgrade" ));
            }
        if ( upgradedCount > 0 )
            if(RvdLoggers.local.isEnabledFor(Level.INFO)) {
                RvdLoggers.local.log(Level.INFO, LoggingHelper.buildMessage(getClass(),"upgradeWorkspace","" + upgradedCount + " RVD projects upgraded"));
            }
        if ( projectNames.size() > 0 && failedCount == 0)
            if(RvdLoggers.local.isEnabledFor(Level.INFO)) {
                RvdLoggers.local.log(Level.INFO, LoggingHelper.buildMessage(getClass(),"upgradeWorkspace","all RVD projects are up to date (or don't need upgrade)"));
            }
        //if ( upgradedCount  0 && projectNames.size() > 0 )
          //  logger.info("All RVD projects are up-to-date" );
    }

    public void backupProjectState(String projectName, WorkspaceStorage storage) throws StorageException {
        try {
            String stateData = storage.loadEntityString("state", projectName);
            storage.storeEntityString(stateData, "state.old", projectName);
        } catch (StorageException e) {
            throw new StorageException("Error creating state file backup", e);
        }

//        File sourceStateFile = new File(storage.rootPath + File.separator + projectName + File.separator + "state");
//        File backupStateFile = new File(storage.rootPath + File.separator + projectName + File.separator + "state" + ".old");
//
//        try {
//            FileUtils.copyFile(sourceStateFile, backupStateFile);
//        } catch (IOException e) {
//            throw new StorageException("Error creating state file backup: " + backupStateFile);
//        }
    }



}

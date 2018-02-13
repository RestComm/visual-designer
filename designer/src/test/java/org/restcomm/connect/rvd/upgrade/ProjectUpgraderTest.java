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

package org.restcomm.connect.rvd.upgrade;

import com.google.gson.JsonElement;
import org.junit.Assert;
import org.junit.Test;
import org.restcomm.connect.rvd.BuildService;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.model.StepMarshaler;
import org.restcomm.connect.rvd.model.project.ProjectState;
import org.restcomm.connect.rvd.storage.FsProjectDao;
import org.restcomm.connect.rvd.storage.FsWorkspaceStorage;
import org.restcomm.connect.rvd.storage.JsonModelStorage;
import org.restcomm.connect.rvd.storage.ProjectDao;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.upgrade.exceptions.UpgradeException;

/**
 * @author Orestis Tsakiridis
 */
public class ProjectUpgraderTest {

    //

    /**
     * IMPORTANT: This test fails the second time it is run through. Make sure the target/test-classes directory
     * is removed before running it again. You can do this manually or through 'mvn clean ...'
     *
     * @throws StorageException
     * @throws UpgradeException
     */
    @Test
    public void testVariousProjectUpgrades() throws StorageException, UpgradeException {
        StepMarshaler marshaler = new StepMarshaler();
        String workspaceDirName = getClass().getResource("./workspace").getFile();
        JsonModelStorage storage = new JsonModelStorage(new FsWorkspaceStorage(workspaceDirName), marshaler);
        ProjectDao projectDao = new FsProjectDao(storage);
        UpgradeService upgradeService = new UpgradeService(storage);
        BuildService buildService = new BuildService(projectDao);

        // check the version changes
        JsonElement rootElement = upgradeService.upgradeProject("project3");
        String upgradedVersion = ProjectUpgrader10to11.getVersion(rootElement);
        Assert.assertEquals("Actual upgraded project version is wrong", RvdConfiguration.RVD_PROJECT_VERSION, upgradedVersion);
        // make sure the project builds also
        ProjectState project = projectDao.loadProject("collectMenuProject");
        buildService.buildProject("project3", project);

        // check the collect/menu digits propert has been converted integer -> string
        rootElement = upgradeService.upgradeProject("collectMenuProject");
        Assert.assertNotNull(rootElement);
        project = projectDao.loadProject("collectMenuProject");
        buildService.buildProject("collectMenuProject",project);
    }
}

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

package org.restcomm.connect.rvd.bootstrap;

import org.junit.Test;
import org.junit.Assert;
import org.restcomm.connect.rvd.TestUtils;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.configuration.RvdConfig;
import org.restcomm.connect.rvd.exceptions.BootstrappingException;
import org.restcomm.connect.rvd.utils.CustomizableRvdConfiguration;

import java.io.File;
import java.util.Random;

/**
 * @author Orestis Tsakiridis
 */
public class WorkspaceBootstrapperTest {
    private static String tempDirLocation = System.getProperty("java.io.tmpdir");

    @Test(expected=RuntimeException.class)
    public void workspaceBootstrapFailsIfRootDirMissing() throws BootstrappingException {
        Random ran = new Random();
        String workspaceLocation = tempDirLocation + "/workspace" + ran.nextInt(10000);
        WorkspaceBootstrapper wb = new WorkspaceBootstrapper(workspaceLocation, workspaceLocation + "/" + RvdConfiguration.TEMPLATES_DIRECTORY_NAME);
    }

    @Test
    public void userDirIsCreated() throws BootstrappingException {
        // create workspace dir
        File workspaceDir = TestUtils.createTempWorkspace();
        CustomizableRvdConfiguration config = new CustomizableRvdConfiguration();
        config.setWorkspaceBasePath(workspaceDir.getPath());
        config.setProjectTemplatesWorkspacePath(workspaceDir.getPath() + File.separator + RvdConfiguration.TEMPLATES_DIRECTORY_NAME);

        String workspaceLocation = workspaceDir.getPath();
        // assert @users dir is created
        WorkspaceBootstrapper wb = new WorkspaceBootstrapper(config);
        wb.run();
        String userDirLocation = workspaceLocation + "/" + RvdConfiguration.USERS_DIRECTORY_NAME;
        File usersDir = new File(userDirLocation);
        Assert.assertTrue("Users directory '" + userDirLocation + "' was not created on workspace bootstrapping.", usersDir.exists() );
        File templatesDir = new File(config.getProjectTemplatesWorkspacePath());
        Assert.assertTrue("Templates directory '" + templatesDir + "' was not created on workspace bootstrapping", templatesDir.exists());

        TestUtils.removeTempWorkspace(workspaceLocation);
    }

}

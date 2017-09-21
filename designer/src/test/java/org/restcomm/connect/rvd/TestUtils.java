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

package org.restcomm.connect.rvd;

import org.apache.commons.io.FileUtils;
import org.mockito.Mockito;
import org.restcomm.connect.rvd.configuration.RvdConfig;
import org.restcomm.connect.rvd.model.ModelMarshaler;
import org.restcomm.connect.rvd.model.project.ProjectState;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import static org.mockito.Mockito.when;

/**
 * Helper class for testing rvd. Contains workspace management functions
 * and RvdConfiguration simulation.
 *
 * @author Orestis Tsakiridis
 */
public class TestUtils {
    public static File createTempWorkspace() {
        return createRandomDir("workspace");
    }

    public static File createRandomDir(String prefix) {
        String tempDirLocation = System.getProperty("java.io.tmpdir");
        Random ran = new Random();
        String workspaceLocation = tempDirLocation + "/" + prefix + ran.nextInt(10000);
        File workspaceDir = new File(workspaceLocation);
        workspaceDir.mkdir();

        return workspaceDir;
    }

    public static void removeTempWorkspace(String workspaceLocation) {
        File workspaceDir = new File(workspaceLocation);
        FileUtils.deleteQuietly(workspaceDir);
    }

    public static File createUsersDirectory(String workspaceLocation) {
        String usersLocation = workspaceLocation + "/@users";
        File usersDir = new File(usersLocation);
        usersDir.mkdir();
        return usersDir;
    }

    // TODO hasen't been tested or used.
    public static File createDefaultProject(String projectName, String owner, File workspaceDir, ModelMarshaler marshaler, RvdConfiguration configuration) throws IOException {
        File projectFile = new File(workspaceDir.getPath() + "/" + projectName);
        projectFile.mkdir();
        String state = marshaler.toData(ProjectState.createEmptyVoice(owner, configuration ));
        FileUtils.writeStringToFile(new File(workspaceDir.getPath() + "/" + projectName + "/state"), state );
        return projectFile;
    }

    public static RvdConfiguration defaultRvdConfiguration() {
        RvdConfiguration configuration = Mockito.mock(RvdConfiguration.class);
        when(configuration.getDefaultHttpMaxConns()).thenReturn(RvdConfiguration.DEFAULT_HTTP_MAX_CONNS);
        when(configuration.getDefaultHttpMaxConnsPerRoute()).thenReturn(RvdConfiguration.DEFAULT_HTTP_MAX_CONNS_PER_ROUTE);
        when(configuration.getDefaultHttpTimeout()).thenReturn(RvdConfiguration.DEFAULT_HTTP_TIMEOUT);
        when(configuration.getDefaultHttpTTL()).thenReturn(RvdConfiguration.DEFAULT_HTTP_TTL);
        when(configuration.getExternalServiceMaxConns()).thenReturn(RvdConfiguration.DEFAULT_ES_MAX_CONNS);
        when(configuration.getExternalServiceMaxConnsPerRoute()).thenReturn(RvdConfiguration.DEFAULT_ES_MAX_CONNS_PER_ROUTE);
        when(configuration.getExternalServiceTimeout()).thenReturn(RvdConfiguration.DEFAULT_ES_TIMEOUT);
        when(configuration.getExternalServiceTTL()).thenReturn(RvdConfiguration.DEFAULT_ES_TTL);
        return configuration;
    }
}

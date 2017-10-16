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

package org.restcomm.connect.rvd;

import org.restcomm.connect.rvd.model.StepJsonDeserializer;
import org.restcomm.connect.rvd.model.StepJsonSerializer;
import org.restcomm.connect.rvd.model.project.Node;
import org.restcomm.connect.rvd.model.project.ProjectState;
import org.restcomm.connect.rvd.model.project.Step;
import org.restcomm.connect.rvd.model.server.NodeName;
import org.restcomm.connect.rvd.model.server.ProjectOptions;
import org.restcomm.connect.rvd.storage.FsProjectStorage;
import org.restcomm.connect.rvd.storage.WorkspaceStorage;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This class is responsible for breaking the project state from a big JSON object to separate files per node/step. The
 * resulting files will be easily processed from the interpreter when the application is run.
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class BuildService {

    protected Gson gson;
    private WorkspaceStorage workspaceStorage;

    public BuildService(WorkspaceStorage workspaceStorage) {
        this.workspaceStorage = workspaceStorage;
        // Parse the big project state object into a nice dto model
        gson = new GsonBuilder()
                .registerTypeAdapter(Step.class, new StepJsonDeserializer())
                .registerTypeAdapter(Step.class, new StepJsonSerializer())
                .create();
    }

    /**
     * Breaks the project state from a big JSON object to separate files per node/step. The resulting files will be easily
     * processed from the interpreter when the application is run.
     *
     * @throws StorageException
     */
    public void buildProject(String projectName, ProjectState projectState) throws StorageException {
        // TODO enable deletion after all cloud projects have been upgraded to 1.13
        //FsProjectStorage.deleteBuiltProjectResources(projectName, workspaceStorage);
        ProjectOptions projectOptions = new ProjectOptions();

        // Save general purpose project information
        // Use the start node name as a default target. We could use a more specialized target too here

        // Build the nodes one by one
        for (Node node : projectState.getNodes()) {
            buildNode(node, projectName);
            NodeName nodeName = new NodeName();
            nodeName.setName(node.getName());
            nodeName.setLabel(node.getLabel());
            projectOptions.getNodeNames().add( nodeName );
        }

        projectOptions.setDefaultTarget(projectState.getHeader().getStartNodeName());
        //if ( projectState.getHeader().getLogging() != null )
        //    projectOptions.setLogging(true);
        // Save the nodename-node-label mapping
        FsProjectStorage.storeProjectOptions(projectOptions, projectName, workspaceStorage);
    }

    public void buildProject(String projectName) throws StorageException {
        ProjectState state = FsProjectStorage.loadProject(projectName, workspaceStorage);
        buildProject(projectName, state);
    }

    private void buildNode(Node node, String projectName) throws StorageException {
        // TODO sanitize node name!
        FsProjectStorage.storeNode(node,projectName,workspaceStorage);
//        FsProjectStorage.storeNodeStepnames(node, projectName, workspaceStorage);
//        // process the steps one-by-one
//        for (Step step : node.getSteps()) {
//            FsProjectStorage.storeNodeStep(step, node, projectName, workspaceStorage);
//        }
    }
}

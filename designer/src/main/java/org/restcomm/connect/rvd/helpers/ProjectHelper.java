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
 *
 */

package org.restcomm.connect.rvd.helpers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.FileUtils;
import org.restcomm.connect.rvd.BuildService;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.RvdContext;
import org.restcomm.connect.rvd.exceptions.InvalidServiceParameters;
import org.restcomm.connect.rvd.exceptions.ProjectDoesNotExist;
import org.restcomm.connect.rvd.exceptions.RvdException;
import org.restcomm.connect.rvd.exceptions.StreamDoesNotFitInFile;
import org.restcomm.connect.rvd.exceptions.project.UnsupportedProjectVersion;
import org.restcomm.connect.rvd.jsonvalidation.ProjectValidator;
import org.restcomm.connect.rvd.jsonvalidation.ValidationErrorItem;
import org.restcomm.connect.rvd.jsonvalidation.ValidationResult;
import org.restcomm.connect.rvd.jsonvalidation.exceptions.ValidationException;
import org.restcomm.connect.rvd.jsonvalidation.exceptions.ValidationFrameworkException;
import org.restcomm.connect.rvd.model.ModelMarshaler;
import org.restcomm.connect.rvd.model.project.Node;
import org.restcomm.connect.rvd.model.project.ProjectState;
import org.restcomm.connect.rvd.model.project.StateHeader;
import org.restcomm.connect.rvd.model.project.Step;
import org.restcomm.connect.rvd.model.client.WavItem;
import org.restcomm.connect.rvd.model.project.RvdProject;
import org.restcomm.connect.rvd.storage.FsProjectStorage;
import org.restcomm.connect.rvd.storage.WorkspaceStorage;
import org.restcomm.connect.rvd.storage.exceptions.BadProjectHeader;
import org.restcomm.connect.rvd.storage.exceptions.StorageEntityNotFound;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.storage.exceptions.WavItemDoesNotExist;
import org.restcomm.connect.rvd.upgrade.UpgradeService;
import org.restcomm.connect.rvd.upgrade.UpgradeService.UpgradabilityStatus;
import org.restcomm.connect.rvd.utils.RvdUtils;
import org.restcomm.connect.rvd.utils.Unzipper;

import java.nio.charset.Charset;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ProjectHelper {

    public enum Status {
        OK, UNKNOWN_VERSION, BAD, TOO_OLD, SHOULD_UPGRADE
    }

    RvdConfiguration configuration;
    WorkspaceStorage workspaceStorage;
    ModelMarshaler marshaler;
    String servletContextPath;

    public ProjectHelper(RvdContext rvdContext, WorkspaceStorage workspaceStorage) {
        this.servletContextPath = rvdContext.getServletContext().getContextPath();
        this.configuration = rvdContext.getConfiguration();
        this.workspaceStorage = workspaceStorage;
        this.marshaler = rvdContext.getMarshaler();
    }

    public ProjectHelper(RvdConfiguration configuration, WorkspaceStorage workspaceStorage, ModelMarshaler marshaler, String servletContextPath) {
        this.configuration = configuration;
        this.workspaceStorage = workspaceStorage;
        this.marshaler = marshaler;
        this.servletContextPath = servletContextPath;
    }

    // Used for testing. TODO create a ProjectHelper interface, ProjectServiceBuilder and separate implementation
    public ProjectHelper() {
    }


    static Status projectStatus(StateHeader header) {
        if (header == null || header.getVersion() == null)
            return Status.BAD;
        try {
            UpgradabilityStatus upgradable = UpgradeService.checkUpgradability(header.getVersion(), RvdConfiguration.RVD_PROJECT_VERSION);
            if (upgradable == UpgradabilityStatus.NOT_NEEDED)
                return Status.OK;
            else
            if (upgradable == UpgradabilityStatus.UPGRADABLE)
                return Status.SHOULD_UPGRADE;
            else
            if (upgradable == UpgradabilityStatus.NOT_SUPPORTED)
                return Status.UNKNOWN_VERSION;
            else
                return Status.BAD; // this should not happen

        } catch (Exception e) {
            return Status.UNKNOWN_VERSION;
        }
    }

    /**
     * Creates a project state object
     *
     * @param projectName
     * @param kind
     * @param owner
     * @return
     * @throws StorageException
     * @throws InvalidServiceParameters
     */
    public ProjectState createProjectObject(String projectName, String kind, String owner) throws StorageException, InvalidServiceParameters {
        if ( !"voice".equals(kind) && !"ussd".equals(kind) && !"sms".equals(kind) )
            throw new InvalidServiceParameters("Invalid project kind specified - '" + kind + "'");

        ProjectState state = null;
        if ( "voice".equals(kind) )
            state = ProjectState.createEmptyVoice(owner, configuration);
        else
        if ( "ussd".equals(kind) )
            state = ProjectState.createEmptyUssd(owner, configuration);
        else
        if ( "sms".equals(kind) )
            state = ProjectState.createEmptySms(owner, configuration);

        return state;
    }

    public ValidationResult validateProject(String stateData) throws RvdException {
        try {
            ProjectValidator validator = new ProjectValidator();
            ValidationResult result = validator.validate(stateData);
            return result;
        } catch (IOException e) {
            throw new RvdException("Internal error while validating raw project",e);
        } catch (ProcessingException e) {
            throw new ValidationFrameworkException("Error while validating raw project",e);
        }
    }

    /**
     * Validates a project semantically.  All validation errors/info found are populated inside
     * the ValidationResult object.
     *
     * Use it after json-schema based validation is done with {@link:validateProject()}.
     *
     * @param project
     * @param result validation status object
     */
    public void validateSemantic(ProjectState project, ValidationResult result) {
        int i = 0;
        int j = 0;
        for (Node node: project.getNodes()) {
            for (Step step: node.getSteps()) {
                String stepPath = new StringBuffer("/nodes/").append(i).append("/steps/").append(j).toString();
                List<ValidationErrorItem> errors = step.validate(stepPath, node);
                if (errors != null && errors.size() > 0)
                    for (ValidationErrorItem error: errors)
                        result.appendError(error);
                j ++;
            }
            i ++;
        }
    }

    public void updateProject(HttpServletRequest request, String projectName, ProjectState existingProject) throws RvdException {
        String stateData = null;
        try {
            stateData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RvdException("Internal error while retrieving raw project",e);
        }

        // json-schema based validation
        ValidationResult validationResult = validateProject(stateData);
        ProjectState state = marshaler.toModel(stateData, ProjectState.class);
        // semantic validation
        validateSemantic(state,validationResult);
        // Make sure the current RVD project version is set
        state.getHeader().setVersion(configuration.RVD_PROJECT_VERSION);
        // preserve project owner
        state.getHeader().setOwner(existingProject.getHeader().getOwner());
        //projectStorage.storeProject(projectName, state, false);
        FsProjectStorage.storeProject(false, state, projectName, workspaceStorage);

        if ( !validationResult.isSuccess() ) {
            throw new ValidationException(validationResult);
        }
    }

    public void deleteProject(String projectName) throws ProjectDoesNotExist, StorageException {
        if (! FsProjectStorage.projectExists(projectName,workspaceStorage))
            throw new ProjectDoesNotExist();
        FsProjectStorage.deleteProject(projectName,workspaceStorage);
    }

    public InputStream archiveProject(String projectName) throws StorageException {
        return FsProjectStorage.archiveProject(projectName,workspaceStorage);
    }

    public void importProjectFromRawArchive(InputStream archiveStream, String applicationSid, String owner) throws RvdException {
        File archiveFile = new File(applicationSid);
        String projectName = FilenameUtils.getBaseName(archiveFile.getName());

        // First unzip to temp dir
        File tempProjectDir;
        try {
            tempProjectDir = RvdUtils.createTempDir();
        } catch (RvdException e) {
            throw new StorageException("Error importing project from archive. Cannot create temp directory for project: " + projectName, e );
        }
        Unzipper unzipper = new Unzipper(tempProjectDir);
        unzipper.unzip(archiveStream);

        importProject(tempProjectDir, applicationSid, owner );
    }

    public String importProject(File tempProjectDir, String suggestedName, String owner) throws RvdException {
        try {
            // check project version for compatibility
            String stateFilename = tempProjectDir.getPath() + "/state";
            FileReader reader = new FileReader(stateFilename);
            JsonParser parser = new JsonParser();
            JsonElement rootElement = parser.parse(reader);
            String version = rootElement.getAsJsonObject().get("header").getAsJsonObject().get("version").getAsString();
            // Create a temporary workspace storage.
            WorkspaceStorage tempStorage = new WorkspaceStorage(tempProjectDir.getParent(), marshaler);
            // is this project compatible (current RVD can open and run without upgrading) ?
            if ( ! UpgradeService.checkBackwardCompatible(version, RvdConfiguration.RVD_PROJECT_VERSION) ) {
                if ( UpgradeService.checkUpgradability(version, RvdConfiguration.RVD_PROJECT_VERSION) == UpgradeService.UpgradabilityStatus.UPGRADABLE ) {
                    UpgradeService upgradeService = new UpgradeService(tempStorage);
                    upgradeService.upgradeProject(tempProjectDir.getName());
                    BuildService buildService = new BuildService(tempStorage);
                    buildService.buildProject(tempProjectDir.getName());
                } else {
                    // project cannot be upgraded
                    throw new UnsupportedProjectVersion("Imported project version (" + version + ") not supported");
                }
            }
            // project is either compatible or was upgraded
            ProjectState state = FsProjectStorage.loadProject(tempProjectDir.getName(), tempStorage);
            state.getHeader().setOwner(owner);
            FsProjectStorage.storeProject(false, state, tempProjectDir.getName(), tempStorage);

            // TODO Make these an atomic action!
            suggestedName = FsProjectStorage.getAvailableProjectName(suggestedName, workspaceStorage);
            FsProjectStorage.createProjectSlot(suggestedName, workspaceStorage);

            FsProjectStorage.importProjectFromDirectory(tempProjectDir, suggestedName, true, workspaceStorage);
            return suggestedName;

        } catch ( UnsupportedProjectVersion e) {
            throw e;
        } catch (Exception e) {
            throw new StorageException("Error importing project from archive.",e);
        } finally {
            FileUtils.deleteQuietly(tempProjectDir);
        }
    }

    public void addWavToProject(String projectName, String wavName, InputStream wavStream) throws StorageException, StreamDoesNotFitInFile {
        FsProjectStorage.storeWav(projectName, wavName, wavStream, workspaceStorage, configuration.getMaxMediaFileSize());
    }

    public List<WavItem> getWavs(String appName) throws StorageException {
        return FsProjectStorage.listWavs(appName, workspaceStorage);
    }

    public void removeWavFromProject(String projectName, String wavName) throws WavItemDoesNotExist {
        FsProjectStorage.deleteWav(projectName, wavName,workspaceStorage);
    }

    /**
     * Loads the project specified into an rvd project object
     * @param projectName
     * @return
     * @throws RvdException
     */

    public RvdProject load(String projectName) throws RvdException {
        String projectJson;
        try {
            projectJson = FsProjectStorage.loadProjectString(projectName, workspaceStorage);
        } catch (StorageEntityNotFound e) {
            throw new ProjectDoesNotExist("Error loading project " + projectName, e);
        }
        RvdProject project = RvdProject.fromJson(projectName, projectJson);
        return project;
    }

    /**
     * Parse the state data of a project and tries to extract and return the header. It uses low level
     * json operations instead of models. Use it when you want to avoid processing the whole json structure
     * for big projects while only interested for the header of the project. It's also more fault tolerant
     * to old/future kinds of projects.
     *
     * TODO investigate whether using JsonParse indeed saves any cycles compared to using json models.
     *
     * @param projectName
     * @param rawState
     * @return
     * @throws StorageException
     */
    public static StateHeader parseHeader(String projectName, String rawState) throws StorageException {
        JsonParser parser = new JsonParser();
        JsonElement header_element = null;
        try {
            header_element = parser.parse(rawState).getAsJsonObject().get("header");
        } catch (JsonSyntaxException e) {
            throw new StorageException("Error loading header for project '" + projectName +"'",e);
        }
        if ( header_element == null )
            throw new BadProjectHeader("No header found. This is probably an old project");

        Gson gson = new Gson();
        StateHeader header = gson.fromJson(header_element, StateHeader.class);

        return header;
    }


}

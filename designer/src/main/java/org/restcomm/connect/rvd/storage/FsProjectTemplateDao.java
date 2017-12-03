package org.restcomm.connect.rvd.storage;

import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.exceptions.NotSupportedFeature;
import org.restcomm.connect.rvd.model.ProjectTemplate;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class FsProjectTemplateDao implements ProjectTemplateDao {

    WorkspaceStorage workspaceStorage;
    RvdConfiguration configuration;

    public FsProjectTemplateDao(WorkspaceStorage workspaceStorage, RvdConfiguration configuration) {
        this.workspaceStorage = workspaceStorage;
        this.configuration = configuration;
    }

    /**
     * Retrieves and returns the specified entity
     *
     * It will throw a StorageEntityNotFound in case this is not found (wrong id or bad templates path in configuration)
     *
     * @param id
     * @return a ProjectTemplate object
     * @throws StorageException
     */
    @Override
    public ProjectTemplate loadProjectTemplate(String id) throws StorageException {
        if (! RvdConfiguration.DEFAULT_TEMPLATES_SUPPORT ) {
            throw new NotSupportedFeature();
        }
        ProjectTemplate projectTemplate = workspaceStorage.loadEntity(id, configuration.getProjectTemplatesWorkspacePath(), ProjectTemplate.class );
        return projectTemplate;
    }

    @Override
    public List<ProjectTemplate> loadProjectTemplates(Integer pageIndex, Integer pageSize, String sortingCriteria) throws StorageException {
        if (! RvdConfiguration.DEFAULT_TEMPLATES_SUPPORT ) {
            throw new NotSupportedFeature();
        }

        List<ProjectTemplate> templates = new ArrayList<ProjectTemplate>();
        List<String> templateIds = workspaceStorage.listIds(configuration.getProjectTemplatesWorkspacePath(), "TL.*");
        for (String id: templateIds) {
            templates.add( loadProjectTemplate(id) );
        }
        return templates;
    }
}

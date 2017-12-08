package org.restcomm.connect.rvd.storage;

import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.exceptions.NotSupportedFeature;
import org.restcomm.connect.rvd.model.ProjectTemplate;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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
        ProjectTemplate projectTemplate = workspaceStorage.loadEntity("project.template", configuration.getProjectTemplatesWorkspacePath() + File.separator + id, ProjectTemplate.class );
        return projectTemplate;
    }

    /**
     * Returns list of ProjectTemplates
     *
     * - Set pageIndex to null to get all results. In that case pageSize is ignored.
     * - page numbering starts from 0 .This pageIndex=0 will render the first page
     * - pageSize should be positive (if pageIndex != null)
     *
     * @param pageIndex
     * @param pageSize How many items should be returned per page.
     * @param sortingCriteria not implemented yet
     * @return a list of ProjectTemplate items (possibly empty)
     * @throws {@link StorageException}, {@link IllegalArgumentException}
     */
    @Override
    public List<ProjectTemplate> loadProjectTemplates(Integer pageIndex, Integer pageSize, String sortingCriteria) throws StorageException {
        if (! RvdConfiguration.DEFAULT_TEMPLATES_SUPPORT ) {
            throw new NotSupportedFeature();
        }
        // validate params
        if (pageIndex != null) {
            if ( (pageIndex < 0) || (pageSize == null) || (pageSize <=0) ) {
                throw new IllegalArgumentException();
            }
        }

        List<ProjectTemplate> templates = new ArrayList<ProjectTemplate>();
        // get all templates
        List<String> templateIds = workspaceStorage.listIds(configuration.getProjectTemplatesWorkspacePath(), "TL.*");
        // calculate page information - start, count
        int start, count;
        if (pageIndex == null) {
            start = 0;
            count = templateIds.size();
        } else {
            start = pageSize * pageIndex;
            count = pageSize;
        }
        // keep only items in the page
        int counted = 0;
        ListIterator<String> i = templateIds.listIterator(start);
        while (i.hasNext() && counted < count) {
            String id = i.next();
            templates.add( loadProjectTemplate(id) );
            counted ++;
        }

        return templates;
    }
}

package org.restcomm.connect.rvd.storage;

import junit.framework.Assert;
import org.junit.Test;
import org.restcomm.connect.rvd.http.PaginatedResults;
import org.restcomm.connect.rvd.model.ProjectTemplate;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.utils.CustomizableRvdConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class FsProjectTemplateDaoTest extends FsDaoTestBase {

    CustomizableRvdConfiguration configuration;
    String templatesDir;

    public FsProjectTemplateDaoTest() {
        templatesDir = getTemplatesDir();
        configuration = new CustomizableRvdConfiguration();
        // set-up templates directory in configuration
        configuration.setProjectTemplatesWorkspacePath(templatesDir);
    }

    @Test
    public void testLoadTemplate() throws IOException, StorageException {
        // create dao
        FsProjectTemplateDao dao = new FsProjectTemplateDao(workspaceStorage, configuration);

        ProjectTemplate projectTemplate = dao.loadProjectTemplate("TL1234");
        Assert.assertNotNull(projectTemplate);
        Assert.assertEquals("TL1234", projectTemplate.getId());
        Assert.assertEquals("Menu application", projectTemplate.getName());
        Assert.assertTrue(projectTemplate.getTags().contains("voice"));
    }

    @Test
    public void testLoadTemplateList() throws StorageException {
        FsProjectTemplateDao dao = new FsProjectTemplateDao(workspaceStorage, configuration);
        PaginatedResults<ProjectTemplate> results = new PaginatedResults<ProjectTemplate>();
        dao.loadProjectTemplates(0, 10, null, results);
        Assert.assertNotNull(results.getResults());
        Assert.assertEquals(2, results.getResults().size());
        // test paging
        results = new PaginatedResults<ProjectTemplate>();
        dao.loadProjectTemplates(0, 10, null, results);
        // should have 2 results
        Assert.assertEquals(2, results.getResults().size());
        // TODO Fix order test. It's not deterministic.
        Assert.assertEquals("TL2222", results.getResults().get(0).getId());
        dao.loadProjectTemplates(1, 1, null, results);
        Assert.assertEquals(1, results.getResults().size());
        Assert.assertEquals("TL1234", results.getResults().get(0).getId());
        // for pages out of index return an empty list
        results = new PaginatedResults<ProjectTemplate>();
        dao.loadProjectTemplates(2, 1, null, results );
        Assert.assertEquals(0, results.getResults().size());
        // setting pageSize to 0 with non-null pageIndex should throw error
        try {
            results = new PaginatedResults<ProjectTemplate>();
            dao.loadProjectTemplates(1, 0, null, results);
            Assert.assertFalse("An IllegalArgumentError should have been thrown", true);
            results = new PaginatedResults<ProjectTemplate>();
            dao.loadProjectTemplates(1, -1, null, results );
            Assert.assertFalse("An IllegalArgumentError should have been thrown", true);
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }

    /**
     * Returns an absolute path to the root template directory in java test resources.
     *
     * Will need TL1234 template to be there
     *
     * @return
     */
    private String getTemplatesDir() {
        // determine actual location of the templates directory
        URL url = this.getClass().getResource("/org/restcomm/connect/rvd/storage/templates/TL1234/project.template");
        String filePath = url.getFile();
        String templatesDir = new File(filePath).getParentFile().getParentFile().getPath();
        return templatesDir;

    }
}

package org.restcomm.connect.rvd.storage;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.TestUtils;
import org.restcomm.connect.rvd.model.ModelMarshaler;
import org.restcomm.connect.rvd.model.ProjectSettings;
import org.restcomm.connect.rvd.model.project.Node;
import org.restcomm.connect.rvd.model.project.ProjectState;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.utils.CustomizableRvdConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class FsProjectDaoTest {

    File workspaceDir;
    WorkspaceStorage workspaceStorage;
    File defaultProjectDir;


    @Before
    public void before() throws IOException {
            workspaceDir = TestUtils.createTempWorkspace();
            ModelMarshaler marshaler = new ModelMarshaler();
            RvdConfiguration configuration = new CustomizableRvdConfiguration();
            defaultProjectDir = TestUtils.createDefaultProject("project1", "orestis", workspaceDir, marshaler, configuration);
            workspaceStorage = new WorkspaceStorage(workspaceDir.getPath(), marshaler);
    }

    @After
    public void after() {
        if (workspaceDir != null) {
            TestUtils.removeTempWorkspace(workspaceDir.getPath());
        }
    }

    @Test
    public void checkProjectResources() throws IOException, StorageException {
        ProjectDao dao = new FsProjectDao("project1", workspaceStorage );

        String rawState = dao.loadProjectStateRaw();
        File stateFile = new File(defaultProjectDir.getPath() + "/state");
        Assert.assertEquals("FsProjectDao.loadProjectStateRaw() failed", FileUtils.readFileToString(stateFile,  Charset.forName("UTF-8")), rawState);
        // Return state object
        ProjectState state = dao.loadProject();
        Assert.assertNotNull(state);
        // State object returned should not be null
        Assert.assertNull(dao.loadSettings());
        // ./settings file should be created
        ProjectSettings projectSettings = new ProjectSettings(true, true);
        dao.storeSettings(projectSettings);
        Assert.assertTrue(new File(defaultProjectDir.getPath() + "/settings").exists());
        // load settings file and check content
        ProjectSettings projectSettings2 = dao.loadSettings();
        Assert.assertTrue("ProjectSettings loading failed", projectSettings2.getLogging()== true && projectSettings2.getLoggingRCML()==true );
        // check name
        Assert.assertEquals("project1", dao.getName());
        // bootstrap info loading. Only test as a string.
        File bootstrapFile = new File(defaultProjectDir.getPath() + "/bootstrap");
        FileUtils.writeStringToFile(bootstrapFile, "[]");
        Assert.assertEquals("[]", dao.loadBootstrapInfo());

        // create build directory and put a node inside it
        File buildDir = new File(defaultProjectDir.getPath() + "/data");
        Assert.assertTrue(buildDir.mkdir());
        File node1File = new File(buildDir.getPath() + "/node1.mod");
        FileUtils.writeStringToFile(node1File, "{\"name\":\"module1\",\"label\":\"Sales\",\"kind\":\"voice\",\"steps\":[]}"); // name: module1, label: Sales, kind: voide, steps:[]
        Node node1 = dao.loadNode("node1");
        Assert.assertNotNull(node1);
        Assert.assertEquals("module1", node1.getName());
    }

    @Test
    public void checkBuiltResources() throws IOException, StorageException {
        ProjectDao dao = new FsProjectDao("project1", workspaceStorage );

        // create build directory and put a node inside it
        File buildDir = new File(defaultProjectDir.getPath() + "/data");
        Assert.assertTrue(buildDir.mkdir());
        File node1File = new File(buildDir.getPath() + "/node1.mod");
        FileUtils.writeStringToFile(node1File, "{\"name\":\"module1\",\"label\":\"Sales\",\"kind\":\"voice\",\"steps\":[]}"); // name: module1, label: Sales, kind: voide, steps:[]
        Node node1 = dao.loadNode("node1");
        Assert.assertNotNull(node1);
        Assert.assertEquals("module1", node1.getName());

    }




}

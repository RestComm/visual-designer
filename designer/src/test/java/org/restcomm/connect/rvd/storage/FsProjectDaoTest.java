package org.restcomm.connect.rvd.storage;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.TestUtils;
import org.restcomm.connect.rvd.model.ProjectSettings;
import org.restcomm.connect.rvd.model.client.WavItem;
import org.restcomm.connect.rvd.model.project.Node;
import org.restcomm.connect.rvd.model.project.ProjectState;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.utils.CustomizableRvdConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class FsProjectDaoTest extends FsDaoTestBase {

    File defaultProjectDir;
    static final String PROJECT_NAME = "project1";
    RvdConfiguration configuration;

    @Before
    public void before() throws IOException {
        configuration = new CustomizableRvdConfiguration();
        defaultProjectDir = TestUtils.createDefaultProject(PROJECT_NAME, "orestis", workspaceDir, marshaler, configuration);
    }

    @Test
    public void checkProjectResources() throws IOException, StorageException {
        ProjectDao dao = new FsProjectDao(oldWorkspaceStorage);

        String rawState = dao.loadProjectStateRaw(PROJECT_NAME);
        File stateFile = new File(defaultProjectDir.getPath() + "/state");
        Assert.assertEquals("FsProjectDao.loadProjectStateRaw() failed", FileUtils.readFileToString(stateFile,  Charset.forName("UTF-8")), rawState);
        // Return state object
        ProjectState state = dao.loadProject(PROJECT_NAME);
        Assert.assertNotNull(state);
        // State object returned should not be null
        Assert.assertNull(dao.loadSettings(PROJECT_NAME));
        // ./settings file should be created
        ProjectSettings projectSettings = new ProjectSettings(true, true);
        dao.storeSettings(projectSettings, PROJECT_NAME);
        Assert.assertTrue(new File(defaultProjectDir.getPath() + "/settings").exists());
        // load settings file and check content
        ProjectSettings projectSettings2 = dao.loadSettings(PROJECT_NAME);
        Assert.assertTrue("ProjectSettings loading failed", projectSettings2.getLogging()== true && projectSettings2.getLoggingRCML()==true );
        // bootstrap info loading. Only test as a string.
        File bootstrapFile = new File(defaultProjectDir.getPath() + "/bootstrap");
        FileUtils.writeStringToFile(bootstrapFile, "[]");
        Assert.assertEquals("[]", dao.loadBootstrapInfo(PROJECT_NAME));

        // create build directory and put a node inside it
        File buildDir = new File(defaultProjectDir.getPath() + "/data");
        Assert.assertTrue(buildDir.mkdir());
        File node1File = new File(buildDir.getPath() + "/node1.mod");
        FileUtils.writeStringToFile(node1File, "{\"name\":\"module1\",\"label\":\"Sales\",\"kind\":\"voice\",\"steps\":[]}"); // name: module1, label: Sales, kind: voide, steps:[]
        Node node1 = dao.loadNode("node1", PROJECT_NAME );
        Assert.assertNotNull(node1);
        Assert.assertEquals("module1", node1.getName());
    }

    @Test
    public void checkBuiltResources() throws IOException, StorageException {
        ProjectDao dao = new FsProjectDao(oldWorkspaceStorage);

        // create build directory and put a node inside it
        File buildDir = new File(defaultProjectDir.getPath() + "/data");
        Assert.assertTrue(buildDir.mkdir());
        File node1File = new File(buildDir.getPath() + "/node1.mod");
        FileUtils.writeStringToFile(node1File, "{\"name\":\"module1\",\"label\":\"Sales\",\"kind\":\"voice\",\"steps\":[]}"); // name: module1, label: Sales, kind: voide, steps:[]
        Node node1 = dao.loadNode("node1", PROJECT_NAME );
        Assert.assertNotNull(node1);
        Assert.assertEquals("module1", node1.getName());

    }

    @Test
    public void checkMediaResources() throws StorageException, IOException {
        ProjectDao dao = new FsProjectDao(oldWorkspaceStorage);
        // no wavs by default
        List<WavItem> wavs = dao.listMedia(PROJECT_NAME);
        Assert.assertEquals(0, wavs.size());

        // create a project with some media
        String projectName = "APprojectwithmedia";
        File projectWithWavs = TestUtils.createProjectWithMedia(projectName, "orestis", workspaceDir, marshaler, configuration);
        wavs = dao.listMedia(projectName);
        Assert.assertEquals(2, wavs.size());
        Assert.assertEquals("onhold.wav", wavs.get(1).getFilename());
        Assert.assertEquals("intro.mp4", wavs.get(0).getFilename());

        // check addRawResource
        String absoluteResourcePath = projectWithWavs.getPath() + File.separator + RvdConfiguration.WAVS_DIRECTORY_NAME;
        ((FsProjectDao) dao).addRawResource(PROJECT_NAME, RvdConfiguration.WAVS_DIRECTORY_NAME, absoluteResourcePath, "onhold.wav" );
        File copiedMediaFile = new File(defaultProjectDir.getPath() + File.separator + RvdConfiguration.WAVS_DIRECTORY_NAME + File.separator + "onhold.wav");
        Assert.assertTrue(copiedMediaFile.exists());
        Assert.assertTrue(FileUtils.readFileToString(copiedMediaFile).contains("dummy contents of a wav file"));
    }

    @Test
    public void checkCreateFromLocation() throws IOException, StorageException {
        ProjectDao dao = new FsProjectDao(oldWorkspaceStorage);
        // create the "remote" project
        String projectName = "APproject2";
        File project2 = TestUtils.createProjectWithMedia(projectName, "orestis", workspaceDir, marshaler, configuration);

        String clonedProject = "APclonedProject";
        dao.createProjectFromLocation(clonedProject, project2.getPath(), "orestis2" );
        // check everything is in place in the cloned project
        ProjectState state = dao.loadProject(clonedProject);
        Assert.assertEquals("orestis2", state.getHeader().getOwner());
        Assert.assertNotNull(state);
        List<WavItem> media = dao.listMedia(clonedProject);
        Assert.assertEquals(2, media.size());
    }





}

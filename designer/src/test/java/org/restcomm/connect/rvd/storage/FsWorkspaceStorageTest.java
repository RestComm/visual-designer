package org.restcomm.connect.rvd.storage;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.restcomm.connect.rvd.TestUtils;
import org.restcomm.connect.rvd.model.StepMarshaler;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import java.io.File;
import java.io.IOException;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class FsWorkspaceStorageTest {
    @Test
    public void testPathResolving() {
        StepMarshaler marshaler = new StepMarshaler();
        JsonModelStorage storage = new JsonModelStorage(new FsWorkspaceStorage("/tmp/rootpath"), marshaler );

        Assert.assertNull(storage.resolveWorkspacePath(null));
        Assert.assertEquals("/an/absolute/path", storage.resolveWorkspacePath("/an/absolute/path"));
        Assert.assertEquals("/tmp/rootpath/a/relative/path", storage.resolveWorkspacePath("a/relative/path"));
        Assert.assertEquals("/tmp/rootpath", storage.resolveWorkspacePath(""));
    }

    @Test
    public void testCopyDirToWorkspace() throws IOException, StorageException {

        // create a test workspace
        File workspaceDir = TestUtils.createTempWorkspace();
        // create an arbitraty directory to copy to workspace
        File sourceDir = TestUtils.createRandomDir("source");
        new File(sourceDir + File.separator + "a.json").createNewFile();

        FsWorkspaceStorage storage = new FsWorkspaceStorage(workspaceDir.getPath());
        storage.copyDirToWorkspace(sourceDir.getPath(), workspaceDir.getPath());
        Assert.assertTrue(new File(workspaceDir.getPath() + File.separator + sourceDir.getName()).exists());
        Assert.assertTrue(new File(workspaceDir.getPath() + File.separator + sourceDir.getName() + File.separator + "a.json").exists());

        FileUtils.deleteDirectory(sourceDir);
        TestUtils.removeTempWorkspace(workspaceDir.getPath());

    }
}

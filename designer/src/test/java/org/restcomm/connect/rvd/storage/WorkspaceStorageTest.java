package org.restcomm.connect.rvd.storage;

import junit.framework.Assert;
import org.junit.Test;
import org.restcomm.connect.rvd.model.ModelMarshaler;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class WorkspaceStorageTest {
    @Test
    public void testPathResolving() {
        ModelMarshaler marshaler = new ModelMarshaler();
        WorkspaceStorage storage = new WorkspaceStorage("/tmp/rootpath", marshaler );

        Assert.assertNull(storage.resolveWorkspacePath(null));
        Assert.assertEquals("/an/absolute/path", storage.resolveWorkspacePath("/an/absolute/path"));
        Assert.assertEquals("/tmp/rootpath/a/relative/path", storage.resolveWorkspacePath("a/relative/path"));
        Assert.assertEquals("/tmp/rootpath", storage.resolveWorkspacePath(""));
    }
}

package org.restcomm.connect.rvd.storage;

import junit.framework.Assert;
import org.junit.Test;
import org.restcomm.connect.rvd.model.StepMarshaler;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class OldWorkspaceStorageTest {
    @Test
    public void testPathResolving() {
        StepMarshaler marshaler = new StepMarshaler();
        OldWorkspaceStorage storage = new OldWorkspaceStorage("/tmp/rootpath", marshaler );

        Assert.assertNull(storage.resolveWorkspacePath(null));
        Assert.assertEquals("/an/absolute/path", storage.resolveWorkspacePath("/an/absolute/path"));
        Assert.assertEquals("/tmp/rootpath/a/relative/path", storage.resolveWorkspacePath("a/relative/path"));
        Assert.assertEquals("/tmp/rootpath", storage.resolveWorkspacePath(""));
    }
}

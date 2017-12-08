package org.restcomm.connect.rvd.storage;

import org.junit.After;
import org.junit.Before;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.TestUtils;
import org.restcomm.connect.rvd.model.ModelMarshaler;
import org.restcomm.connect.rvd.utils.CustomizableRvdConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class FsDaoTestBase {

    File workspaceDir;
    WorkspaceStorage workspaceStorage;
    ModelMarshaler marshaler;

    FsDaoTestBase() {
        workspaceDir = TestUtils.createTempWorkspace();
        marshaler = new ModelMarshaler();
        workspaceStorage = new WorkspaceStorage(workspaceDir.getPath(), marshaler);
    }


    @After
    public void after() {
        if (workspaceDir != null) {
            TestUtils.removeTempWorkspace(workspaceDir.getPath());
        }
    }
}

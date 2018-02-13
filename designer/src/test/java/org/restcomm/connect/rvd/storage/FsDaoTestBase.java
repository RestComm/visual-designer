package org.restcomm.connect.rvd.storage;

import org.junit.After;
import org.restcomm.connect.rvd.TestUtils;
import org.restcomm.connect.rvd.model.StepMarshaler;

import java.io.File;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class FsDaoTestBase {

    File workspaceDir;
    JsonModelStorage storage;
    StepMarshaler marshaler;

    FsDaoTestBase() {
        workspaceDir = TestUtils.createTempWorkspace();
        marshaler = new StepMarshaler();
        storage = new JsonModelStorage(new FsWorkspaceStorage(workspaceDir.getPath()), marshaler);
    }


    @After
    public void after() {
        if (workspaceDir != null) {
            TestUtils.removeTempWorkspace(workspaceDir.getPath());
        }
    }
}

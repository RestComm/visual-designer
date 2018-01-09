package org.restcomm.connect.rvd.interpreter;

import junit.framework.Assert;
import org.junit.Test;
import org.restcomm.connect.rvd.model.steps.StepTestBase;
import org.restcomm.connect.rvd.storage.FsProjectDao;
import org.restcomm.connect.rvd.storage.ProjectDao;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.utils.CustomizableRvdConfiguration;

import javax.ws.rs.core.MultivaluedMap;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class VariablesAndParametersTest extends StepTestBase {

    @Test
    public void rcommFromParamResolving() throws StorageException {
        MultivaluedMap<String,String> params = appendMultivaluedMap(null, "From", "1234");
        buildApplicationContext(new CustomizableRvdConfiguration());
        Interpreter interpreter = buildInterpreter(params, null);
        Assert.assertEquals("1234", interpreter.populateVariables("$core_From"));
    }

}

package org.restcomm.connect.rvd.model.steps.redirect;

import junit.framework.Assert;
import org.junit.Test;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.model.steps.StepTestBase;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class RedirectStepTest extends StepTestBase {

    /**
     * Check Redirect properties
     */
    @Test
    public void defaultRendering() throws StorageException {
        Interpreter interpreter = buildInterpreter(appendMultivaluedMap(null,null,null), null);
        RedirectStep step = new RedirectStep();
        step.setUrl("http://localhost");
        RcmlRedirectStep rcmlStep = (RcmlRedirectStep)step.render(interpreter, null);
        Assert.assertEquals("http://localhost", rcmlStep.getUrl());
        Assert.assertNull(rcmlStep.getMethod());

        step = new RedirectStep();
        step.setNext("mymodule");
        step.setMethod("POST");
        rcmlStep = (RcmlRedirectStep)step.render(interpreter, null);
        Assert.assertEquals("controller?target=mymodule", rcmlStep.getUrl());
        Assert.assertEquals("POST", rcmlStep.getMethod());
    }
}

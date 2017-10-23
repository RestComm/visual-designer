package org.restcomm.connect.rvd.model.steps.say;

import junit.framework.Assert;
import org.junit.Test;
import org.restcomm.connect.rvd.ApplicationContext;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.logging.MockedCustomLogger;
import org.restcomm.connect.rvd.logging.ProjectLogger;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.model.ProjectSettings;
import org.restcomm.connect.rvd.model.steps.StepTestBase;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class SayStepTest extends StepTestBase {

    Interpreter interpreter;

    public SayStepTest() {
    }

    @Test
    public void defaultRendering() {
        Interpreter interpreter = buildInterpreter(appendMultivaluedMap(null,null,null));
        SayStep step = new SayStep("hello world", "man", "en", 1);
        RcmlSayStep rcmlStep = (RcmlSayStep)step.render(interpreter, null);
        Assert.assertEquals("hello world", rcmlStep.getPhrase());
        Assert.assertEquals("en", rcmlStep.getLanguage());
        Assert.assertEquals("man", rcmlStep.getVoice());
    }

    @Test
    public void phraseWithVariables() {
        Interpreter interpreter = buildInterpreter(appendMultivaluedMap(null, RvdConfiguration.MODULE_PREFIX + "name","alice"));
        SayStep step = new SayStep("hello $name", null, null, null);
        RcmlSayStep rcmlStep = (RcmlSayStep)step.render(interpreter, null);
        Assert.assertEquals("hello alice", rcmlStep.getPhrase());
    }
}

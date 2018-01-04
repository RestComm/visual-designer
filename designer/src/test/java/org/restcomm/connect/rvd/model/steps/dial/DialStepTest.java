package org.restcomm.connect.rvd.model.steps.dial;

import junit.framework.Assert;
import org.junit.Test;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.model.steps.StepTestBase;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.utils.CustomizableRvdConfiguration;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class DialStepTest extends StepTestBase {

    /**
     * Check rendering or Dial step and nouns
     */
    @Test
    public void defaultRendering() throws StorageException, InterpreterException {
        buildApplicationContext(new CustomizableRvdConfiguration());
        ((CustomizableRvdConfiguration)appContext.getConfiguration()).setVideoSupport(true);
        Interpreter interpreter = buildInterpreter(appendMultivaluedMap(null,null,null), null);
        DialStep step = new DialStep();
        // add client noun
        ClientDialNoun clientNoun = new ClientDialNoun("alice");
        clientNoun.enableVideo = true;
        clientNoun.videoOverlay = "1234";
        clientNoun.videoResolution = "VGA"; // one of CIF|4CIF|16CIF|QCIF|VGA|720p
        step.dialNouns.add(clientNoun);
        // add sip-uri noun
        SipuriDialNoun sipNoun = new SipuriDialNoun("sip:alice");
        sipNoun.enableVideo = true;
        sipNoun.videoOverlay = "4321";
        sipNoun.videoResolution = "CIF"; // one of CIF|4CIF|16CIF|QCIF|VGA|720p
        step.dialNouns.add(sipNoun);

        RcmlDialStep rcmlStep = (RcmlDialStep)step.render(interpreter, null);
        // client noun video properties
        Assert.assertEquals("alice", ((RcmlClientNoun)rcmlStep.nouns.get(0)).getDestination());
        Assert.assertNotNull(((RcmlClientNoun)rcmlStep.nouns.get(0)).video);
        Assert.assertEquals(Boolean.TRUE, ((RcmlClientNoun)rcmlStep.nouns.get(0)).video.enable);
        Assert.assertEquals("1234", ((RcmlClientNoun)rcmlStep.nouns.get(0)).video.overlay);
        Assert.assertEquals("VGA", ((RcmlClientNoun)rcmlStep.nouns.get(0)).video.resolution);
        // sip uri noun video properties
        Assert.assertEquals("sip:alice", ((RcmlSipuriNoun)rcmlStep.nouns.get(1)).getDestination());
        Assert.assertNotNull(((RcmlSipuriNoun)rcmlStep.nouns.get(1)).video);
        Assert.assertEquals(Boolean.TRUE, ((RcmlSipuriNoun)rcmlStep.nouns.get(1)).video.enable);
        Assert.assertEquals("4321", ((RcmlSipuriNoun)rcmlStep.nouns.get(1)).video.overlay);
        Assert.assertEquals("CIF", ((RcmlSipuriNoun)rcmlStep.nouns.get(1)).video.resolution);
        // TODO test the rest of dial properties
        // ...
    }

//    /**
//     * Checks if a phrase of a Say step supports variables.
//     */
//    @Test
//    public void phraseWithModuleVariable() throws StorageException {
//        Interpreter interpreter = buildInterpreter(appendMultivaluedMap(null,null,null),null);
//        interpreter.getVariables().put(RvdConfiguration.MODULE_PREFIX + "name", "alice");
//        SayStep step = new SayStep("hello $name", null, null, null);
//        RcmlSayStep rcmlStep = (RcmlSayStep)step.render(interpreter, null);
//        Assert.assertEquals("hello alice", rcmlStep.getPhrase());
//    }
}

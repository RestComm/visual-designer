package org.restcomm.connect.rvd.model.steps.say;

import org.junit.Test;
import org.restcomm.connect.rvd.ApplicationContext;
import org.restcomm.connect.rvd.interpreter.Interpreter;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class SayStepTest {

    Interpreter interpreter;

    public SayStepTest() {
        ApplicationContext appContext = new ApplicationContext();
        //Interpreter interpreter = new Interpreter();
        //...
    }

    @Test
    public void defaultRendering() {
        SayStep step = new SayStep();
        //...
    }
}

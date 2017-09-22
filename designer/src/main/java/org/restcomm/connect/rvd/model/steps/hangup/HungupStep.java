package org.restcomm.connect.rvd.model.steps.hangup;

import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.model.project.Step;
import org.restcomm.connect.rvd.model.rcml.RcmlStep;

public class HungupStep extends Step {

    @Override
    public RcmlStep render(Interpreter interpreter, String containerModule) throws InterpreterException {
        return new RcmlHungupStep();
    }

}

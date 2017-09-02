package org.restcomm.connect.rvd.interpreter.steps;

import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.DefaultStepBehavior;
import org.restcomm.connect.rvd.interpreter.InterpretableStep;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.interpreter.Target;
import org.restcomm.connect.rvd.interpreter.rcml.Rcml;
import org.restcomm.connect.rvd.interpreter.rcml.RcmlHungupStep;
import org.restcomm.connect.rvd.model.steps.hangup.HungupStep;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class InterpretedHungupStep extends HungupStep implements InterpretableStep {

    static InterpretableStep defaultInterpretableStep = new DefaultStepBehavior();

    @Override
    public Rcml render(Interpreter interpreter) throws InterpreterException {
        return new RcmlHungupStep();
    }

    @Override
    public void handleAction(Interpreter interpreter, Target originTarget) throws InterpreterException, StorageException {
        defaultInterpretableStep.handleAction(interpreter, originTarget);
    }

    @Override
    public String process(Interpreter interpreter, HttpServletRequest httpRequest) throws InterpreterException {
        return defaultInterpretableStep.process(interpreter, httpRequest);
    }
}

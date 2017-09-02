package org.restcomm.connect.rvd.interpreter.steps;

import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.DefaultStepBehavior;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.interpreter.InterpretableStep;
import org.restcomm.connect.rvd.interpreter.Target;
import org.restcomm.connect.rvd.interpreter.rcml.Rcml;
import org.restcomm.connect.rvd.interpreter.rcml.RcmlSayStep;
import org.restcomm.connect.rvd.model.steps.say.SayStep;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class InterpretedSayStep extends SayStep implements InterpretableStep {

    static InterpretableStep defaultInterpretableStep = new DefaultStepBehavior();

    public InterpretedSayStep(String phrase) {
        super(phrase);
    }

    @Override
    public Rcml render(Interpreter interpreter) {

        RcmlSayStep sayStep = new RcmlSayStep();
        sayStep.setPhrase(interpreter.populateVariables(getPhrase()));
        sayStep.setVoice(getVoice());
        sayStep.setLanguage(getLanguage());
        sayStep.setLoop(getLoop());

        return sayStep;
    }

    @Override
    public void handleAction(Interpreter interpreter, Target originTarget) throws InterpreterException, StorageException {
        defaultInterpretableStep.handleAction(interpreter,originTarget);
    }

    @Override
    public String process(Interpreter interpreter, HttpServletRequest httpRequest) throws InterpreterException {
        return defaultInterpretableStep.process(interpreter, httpRequest);
    }

}

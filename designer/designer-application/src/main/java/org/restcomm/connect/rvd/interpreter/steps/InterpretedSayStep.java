package org.restcomm.connect.rvd.interpreter.steps;

import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.DefaultStepBehavior;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.interpreter.StepBehavior;
import org.restcomm.connect.rvd.interpreter.Target;
import org.restcomm.connect.rvd.jsonvalidation.ValidationErrorItem;
import org.restcomm.connect.rvd.model.project.Node;
import org.restcomm.connect.rvd.model.rcml.RcmlStep;
import org.restcomm.connect.rvd.interpreter.rcml.RcmlSayStep;
import org.restcomm.connect.rvd.model.steps.say.SayStep;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class InterpretedSayStep extends SayStep implements StepBehavior {

    static StepBehavior defaultStepBehavior = new DefaultStepBehavior();

    public InterpretedSayStep(String phrase) {
        super(phrase);
    }

    @Override
    public RcmlStep render(Interpreter interpreter) {

        RcmlSayStep sayStep = new RcmlSayStep();
        sayStep.setPhrase(interpreter.populateVariables(getPhrase()));
        sayStep.setVoice(getVoice());
        sayStep.setLanguage(getLanguage());
        sayStep.setLoop(getLoop());

        return sayStep;
    }

    @Override
    public void handleAction(Interpreter interpreter, Target originTarget) throws InterpreterException, StorageException {
        defaultStepBehavior.handleAction(interpreter,originTarget);
    }

    @Override
    public String process(Interpreter interpreter, HttpServletRequest httpRequest) throws InterpreterException {
        return defaultStepBehavior.process(interpreter, httpRequest);
    }

    @Override
    public List<ValidationErrorItem> validate(String stepPath, Node parentModule) {
        return defaultStepBehavior.validate(stepPath, parentModule);
    }
}

package org.restcomm.connect.rvd.interpreter.steps;

import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.DefaultStepBehavior;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.interpreter.StepBehavior;
import org.restcomm.connect.rvd.interpreter.Target;
import org.restcomm.connect.rvd.validation.ValidationErrorItem;
import org.restcomm.connect.rvd.model.project.Node;
import org.restcomm.connect.rvd.interpreter.rcml.UssdSayRcml;
import org.restcomm.connect.rvd.model.steps.ussdsay.UssdSayStep;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class InterpretedUssdSayStep extends UssdSayStep implements StepBehavior {

    static StepBehavior defaultStepBehavior = new DefaultStepBehavior();

    public InterpretedUssdSayStep(String text) {
        super(text);
    }

    @Override
    public UssdSayRcml render(Interpreter interpreter) throws InterpreterException {
        UssdSayRcml rcmlModel = new UssdSayRcml();
        rcmlModel.setText(interpreter.populateVariables(getText()));

        return rcmlModel;
    }

    @Override
    public void handleAction(Interpreter interpreter, Target originTarget) throws InterpreterException, StorageException {
        defaultStepBehavior.handleAction(interpreter,originTarget);
    }

    @Override
    public String process(Interpreter interpreter, HttpServletRequest httpRequest) throws InterpreterException {
        return defaultStepBehavior.process(interpreter,httpRequest);
    }

}

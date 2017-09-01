package org.restcomm.connect.rvd.interpreter;

import org.apache.commons.lang.NotImplementedException;
import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.exceptions.RVDUnsupportedHandlerVerb;
import org.restcomm.connect.rvd.interpreter.rcml.Rcml;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import javax.servlet.http.HttpServletRequest;

/**
 * Defines default behavior regarding actions, validation and process() in case a step does not define them
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class DefaultStepBehavior implements InterpretableStep {
    @Override
    public Rcml render(Interpreter interpreter) {
        throw new NotImplementedException();
    }

    @Override
    public void handleAction(Interpreter interpreter, Target originTarget) throws InterpreterException, StorageException {
        throw new RVDUnsupportedHandlerVerb();
    }

    @Override
    public String process(Interpreter interpreter, HttpServletRequest httpRequest) throws InterpreterException {
        return null;
    }

}

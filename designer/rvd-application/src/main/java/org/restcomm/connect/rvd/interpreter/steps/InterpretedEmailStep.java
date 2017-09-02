package org.restcomm.connect.rvd.interpreter.steps;

import org.apache.log4j.Level;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.DefaultStepBehavior;
import org.restcomm.connect.rvd.interpreter.InterpretableStep;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.interpreter.Target;
import org.restcomm.connect.rvd.interpreter.rcml.Rcml;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.logging.system.LoggingHelper;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.steps.email.EmailStep;
import org.restcomm.connect.rvd.interpreter.rcml.RcmlEmailStep;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.utils.RvdUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class InterpretedEmailStep extends EmailStep implements InterpretableStep {

    static InterpretableStep defaultInterpretableStep = new DefaultStepBehavior();

    @Override
    public Rcml render(Interpreter interpreter) throws InterpreterException {
        RcmlEmailStep rcmlStep = new RcmlEmailStep();

        if ( ! RvdUtils.isEmpty(getNext()) ) {
            String newtarget = interpreter.getTarget().getNodename() + "." + getName() + ".actionhandler";
            Map<String, String> pairs = new HashMap<String, String>();
            pairs.put("target", newtarget);
            String action = interpreter.buildAction(pairs);
            rcmlStep.setAction(action);
            rcmlStep.setMethod(getMethod());
        }

        rcmlStep.setFrom(interpreter.populateVariables(getFrom()));
        rcmlStep.setSubject(interpreter.populateVariables(getSubject()));
        rcmlStep.setTo(interpreter.populateVariables(getTo()));
        rcmlStep.setCc(interpreter.populateVariables(getCc()));
        rcmlStep.setBcc(interpreter.populateVariables(getBcc()));
        rcmlStep.setStatusCallback(getStatusCallback());
        rcmlStep.setText(interpreter.populateVariables(getText()));

        return rcmlStep;

    }

    @Override
    public void handleAction(Interpreter interpreter, Target originTarget) throws InterpreterException, StorageException {
        LoggingContext logging = interpreter.getRvdContext().logging;
        if (RvdLoggers.local.isEnabledFor(Level.INFO))
            RvdLoggers.local.log(Level.INFO, LoggingHelper.buildMessage(getClass(),"handleAction", logging.getPrefix(), "handling email action"));

        if ( RvdUtils.isEmpty(getNext()) )
            throw new InterpreterException( "'next' module is not defined for step " + getName() );

        String EmailSid = interpreter.getRequestParams().getFirst("EmailSid");
        String EmailStatus = interpreter.getRequestParams().getFirst("EmailStatus");

        if ( EmailSid != null )
            interpreter.getVariables().put(RvdConfiguration.CORE_VARIABLE_PREFIX + "EmailSid", EmailSid);
        if (EmailStatus != null )
            interpreter.getVariables().put(RvdConfiguration.CORE_VARIABLE_PREFIX + "EmailStatus", EmailStatus);

        interpreter.interpret( getNext(), null, null, originTarget );
    }

    @Override
    public String process(Interpreter interpreter, HttpServletRequest httpRequest) throws InterpreterException {
        return defaultInterpretableStep.process(interpreter,httpRequest);
    }
}

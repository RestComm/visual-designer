package org.restcomm.connect.rvd.interpreter.steps;

import org.apache.log4j.Level;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.DefaultStepBehavior;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.interpreter.StepBehavior;
import org.restcomm.connect.rvd.interpreter.Target;
import org.restcomm.connect.rvd.validation.ValidationErrorItem;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.logging.system.LoggingHelper;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.project.Node;
import org.restcomm.connect.rvd.interpreter.rcml.RcmlSmsStep;
import org.restcomm.connect.rvd.model.steps.sms.SmsStep;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.utils.RvdUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class InterpretedSmsStep extends SmsStep implements StepBehavior {

    static StepBehavior defaultStepBehavior = new DefaultStepBehavior();

    public InterpretedSmsStep(String phrase) {
        super(phrase);
    }

    public RcmlSmsStep render(Interpreter interpreter) {
        RcmlSmsStep rcmlStep = new RcmlSmsStep();

        if ( ! RvdUtils.isEmpty(getNext()) ) {
            String newtarget = interpreter.getTarget().getNodename() + "." + getName() + ".actionhandler";
            Map<String, String> pairs = new HashMap<String, String>();
            pairs.put("target", newtarget);
            String action = interpreter.buildAction(pairs);
            rcmlStep.setAction(action);
            rcmlStep.setMethod(getMethod());
        }

        rcmlStep.setFrom(interpreter.populateVariables(getFrom()));
        rcmlStep.setTo(interpreter.populateVariables(getTo()));
        rcmlStep.setStatusCallback(getStatusCallback());
        rcmlStep.setText(interpreter.populateVariables(getText()));

        return rcmlStep;
    }

    @Override
    public void handleAction(Interpreter interpreter, Target originTarget) throws InterpreterException, StorageException {
        LoggingContext logging = interpreter.getRvdContext().logging;
        if (RvdLoggers.local.isEnabledFor(Level.INFO))
            RvdLoggers.local.log(Level.INFO, LoggingHelper.buildMessage(getClass(),"handleAction", logging.getPrefix(), "handling sms action"));

        if ( RvdUtils.isEmpty(getNext()) )
            throw new InterpreterException( "'next' module is not defined for step " + getName() );

        String SmsSid = interpreter.getRequestParams().getFirst("SmsSid");
        String SmsStatus = interpreter.getRequestParams().getFirst("SmsStatus");

        if ( SmsSid != null )
            interpreter.getVariables().put(RvdConfiguration.CORE_VARIABLE_PREFIX + "SmsSid", SmsSid);
        if (SmsStatus != null )
            interpreter.getVariables().put(RvdConfiguration.CORE_VARIABLE_PREFIX + "SmsStatus", SmsStatus);

        interpreter.interpret( getNext(), null, null, originTarget );
    }

    @Override
    public String process(Interpreter interpreter, HttpServletRequest httpRequest) throws InterpreterException {
        return defaultStepBehavior.process(interpreter, httpRequest);
    }

}

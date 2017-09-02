package org.restcomm.connect.rvd.interpreter.steps;

import org.apache.log4j.Level;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.DefaultStepBehavior;
import org.restcomm.connect.rvd.interpreter.InterpretableStep;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.interpreter.Target;
import org.restcomm.connect.rvd.interpreter.rcml.Rcml;
import org.restcomm.connect.rvd.interpreter.rcml.UssdCollectRcml;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.logging.system.LoggingHelper;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.steps.ussdcollect.UssdCollectStep;
import org.restcomm.connect.rvd.model.steps.ussdsay.UssdSayStep;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class InterpretedUssdCollectStep extends UssdCollectStep implements InterpretableStep {

    static InterpretableStep defaultInterpretableStep = new DefaultStepBehavior();

    @Override
    public Rcml render(Interpreter interpreter) throws InterpreterException {
        // TODO Auto-generated method stub
        UssdCollectRcml rcml = new UssdCollectRcml();
        String newtarget = interpreter.getTarget().getNodename() + "." + getName() + ".handle";
        Map<String, String> pairs = new HashMap<String, String>();
        pairs.put("target", newtarget);

        rcml.action = interpreter.buildAction(pairs);
        for ( UssdSayStep message : messages ) {
            rcml.messages.add(((InterpretableStep)message).render(interpreter));
        }

        return rcml;
    }

    @Override
    public void handleAction(Interpreter interpreter, Target originTarget) throws InterpreterException, StorageException {
        LoggingContext logging = interpreter.getRvdContext().logging;
        if (RvdLoggers.local.isEnabledFor(Level.INFO))
            RvdLoggers.local.log(Level.INFO, LoggingHelper.buildMessage(getClass(),"handleAction", logging.getPrefix(), "handling UssdCollect action"));

        if ("menu".equals(gatherType)) {

            boolean handled = false;
            for (Mapping mapping : menu.mappings) {
                // use a string for USSD collect. Alpha is supported too
                String digits = interpreter.getRequestParams().getFirst("Digits");

                if (RvdLoggers.local.isTraceEnabled())
                    RvdLoggers.local.log(Level.TRACE, LoggingHelper.buildMessage(getClass(),"handleAction","{0} checking digits {1} - {2}", new Object[] {logging.getPrefix(), mapping.digits, digits }));

                if (mapping.digits != null && mapping.digits.equals(digits)) {
                    // seems we found out menu selection
                    if (RvdLoggers.local.isTraceEnabled())
                        RvdLoggers.local.log(Level.TRACE, LoggingHelper.buildMessage(getClass(),"handleAction","{0} seems we found our menu selection", new Object[] {logging.getPrefix(), digits}));
                    interpreter.interpret(mapping.next,null,null, originTarget);
                    handled = true;
                }
            }
            if (!handled) {
                interpreter.interpret(interpreter.getTarget().getNodename() + "." + interpreter.getTarget().getStepname(),null,null, originTarget);
            }
        }
        if ("collectdigits".equals(gatherType)) {
            String variableName = collectdigits.collectVariable;
            String variableValue = interpreter.getRequestParams().getFirst("Digits");
            if ( variableValue == null ) {
                RvdLoggers.local.log(Level.WARN, LoggingHelper.buildMessage(getClass(),"handleAction",logging.getPrefix(),"'Digits' parameter was null. Is this a valid restcomm request?"));
                variableValue = "";
            }

            // is this an application-scoped variable ?
            if ( "application".equals(collectdigits.scope) ) {
                // if it is, create a sticky_* variable named after it
                interpreter.getVariables().put(RvdConfiguration.STICKY_PREFIX + variableName, variableValue);
            }
            // in any case initialize the module-scoped variable
            interpreter.getVariables().put(variableName, variableValue);

            interpreter.interpret(collectdigits.next,null,null, originTarget);
        }
    }

    @Override
    public String process(Interpreter interpreter, HttpServletRequest httpRequest) throws InterpreterException {
        return defaultInterpretableStep.process(interpreter, httpRequest);
    }
}

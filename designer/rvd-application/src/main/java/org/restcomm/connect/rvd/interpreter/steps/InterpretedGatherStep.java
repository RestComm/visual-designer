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
import org.restcomm.connect.rvd.model.project.BaseStep;
import org.restcomm.connect.rvd.model.steps.gather.GatherStep;
import org.restcomm.connect.rvd.interpreter.rcml.RcmlGatherStep;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class InterpretedGatherStep extends GatherStep implements InterpretableStep {

    static InterpretableStep defaultInterpretableStep = new DefaultStepBehavior();

    public Rcml render(Interpreter interpreter) throws InterpreterException {

        RcmlGatherStep rcmlStep = new RcmlGatherStep();
        String newtarget = interpreter.getTarget().getNodename() + "." + getName() + ".handle";
        Map<String, String> pairs = new HashMap<String, String>();
        pairs.put("target", newtarget);
        String action = interpreter.buildAction(pairs);

        rcmlStep.setAction(action);
        rcmlStep.setTimeout(timeout);
        if (finishOnKey != null && !"".equals(finishOnKey))
            rcmlStep.setFinishOnKey(finishOnKey);
        rcmlStep.setMethod(method);
        rcmlStep.setNumDigits(numDigits);

        for (BaseStep nestedStep : steps)
            rcmlStep.getSteps().add(((InterpretableStep)nestedStep).render(interpreter));

        return rcmlStep;
    }

    public void handleAction(Interpreter interpreter, Target originTarget) throws InterpreterException, StorageException {
        LoggingContext logging = interpreter.getRvdContext().logging;
        if (RvdLoggers.local.isEnabledFor(Level.INFO))
            RvdLoggers.local.log(Level.INFO, LoggingHelper.buildMessage(getClass(),"handleAction", logging.getPrefix(), "handling gather action"));

        String digitsString = interpreter.getRequestParams().getFirst("Digits");
        if ( digitsString != null )
            interpreter.getVariables().put(RvdConfiguration.CORE_VARIABLE_PREFIX + "Digits", digitsString);

        boolean valid = true;

        if ("menu".equals(gatherType)) {
            boolean handled = false;
            for (Mapping mapping : menu.mappings) {
                String digits = digitsString;
                //Integer digits = Integer.parseInt( digitsString );
                if (RvdLoggers.local.isTraceEnabled())
                    RvdLoggers.local.log(Level.TRACE, LoggingHelper.buildMessage(getClass(), "handleAction","{0}checking digits: {1} - {2}", new Object[] {logging.getPrefix(), mapping.digits, digits}));

                if (mapping.digits != null && mapping.digits.equals(digits)) {
                    // seems we found out menu selection
                    if (RvdLoggers.local.isTraceEnabled())
                        RvdLoggers.local.log(Level.TRACE, LoggingHelper.buildMessage(getClass(),"handleAction", logging.getPrefix(), " seems we found our menu selection: " + digits));
                    interpreter.interpret(mapping.next,null, null, originTarget);
                    handled = true;
                }
            }
            if (!handled)
                valid = false;
        } else
        if ("collectdigits".equals(gatherType)) {

            String variableName = collectdigits.collectVariable;
            String variableValue = interpreter.getRequestParams().getFirst("Digits");
            if ( variableValue == null ) {

                RvdLoggers.local.log(Level.WARN, LoggingHelper.buildMessage(getClass(),"handleAction", logging.getPrefix(), "'Digits' parameter was null. Is this a valid restcomm request?"));
                variableValue = "";
            }

            // validation
            boolean doValidation = false;
            if ( validation != null ) {
                //if ( validation.pattern != null && !validation.pattern.trim().equals("")) {
                String effectivePattern = null;
                if ( validation.userPattern != null ) {
                    String expandedUserPattern = interpreter.populateVariables(validation.userPattern);
                    effectivePattern = "^[" + expandedUserPattern + "]$";
                }
                else
                if (validation.regexPattern != null ) {
                    String expandedRegexPattern = interpreter.populateVariables(validation.regexPattern);
                    effectivePattern = expandedRegexPattern;
                }
                else {

                    RvdLoggers.local.log(Level.WARN, LoggingHelper.buildMessage(getClass(),"handleAction", logging.getPrefix(),  " Invalid validation information in gather. Validation object exists while other patterns are null"));
                }
                if (effectivePattern != null ) {
                    doValidation = true;
                    if (RvdLoggers.local.isTraceEnabled())
                        RvdLoggers.local.log(Level.TRACE, LoggingHelper.buildMessage(getClass(), "handleAction","{0} validating '{1}' against '{}'", new Object[] {logging.getPrefix(),variableValue, effectivePattern}));
                    if ( !variableValue.matches(effectivePattern) )
                        valid = false;
                }
            }

            if ( doValidation && !valid ) {
                if (RvdLoggers.local.isTraceEnabled())
                    RvdLoggers.local.log(Level.TRACE, LoggingHelper.buildMessage(getClass(), "handleAction", logging.getPrefix(), "{0} Invalid input for gather/collectdigits. Will say the validation message and rerun the gather"));
            } else {
                // is this an application-scoped variable ?
                if ( "application".equals(collectdigits.scope) ) {
                    interpreter.putStickyVariable(variableName, variableValue);
                } else
                if ( "module".equals(collectdigits.scope) ) {
                    interpreter.putModuleVariable(variableName, variableValue);
                }

                // in any case initialize the module-scoped variable
                interpreter.getVariables().put(variableName, variableValue);
                interpreter.interpret(collectdigits.next,null,null, originTarget);
            }
        }

        if ( !valid ) { // this should always be true
            interpreter.interpret(interpreter.getTarget().getNodename() + "." + interpreter.getTarget().getStepname(),null, (InterpretableStep) (( invalidMessage != null ) ? invalidMessage : null), originTarget);
        }
    }

    @Override
    public String process(Interpreter interpreter, HttpServletRequest httpRequest) throws InterpreterException {
        return defaultInterpretableStep.process(interpreter, httpRequest);
    }

}

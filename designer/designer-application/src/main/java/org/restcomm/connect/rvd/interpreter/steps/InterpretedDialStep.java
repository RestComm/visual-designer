package org.restcomm.connect.rvd.interpreter.steps;

import org.apache.log4j.Level;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.DefaultStepBehavior;
import org.restcomm.connect.rvd.interpreter.Interpretable;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.interpreter.InterpretableStep;
import org.restcomm.connect.rvd.interpreter.Target;
import org.restcomm.connect.rvd.interpreter.rcml.Rcml;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.logging.system.LoggingHelper;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.steps.dial.BaseDialNoun;
import org.restcomm.connect.rvd.model.steps.dial.DialStep;
import org.restcomm.connect.rvd.interpreter.rcml.RcmlDialStep;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.utils.RvdUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class InterpretedDialStep extends DialStep implements InterpretableStep {

    static InterpretableStep defaultInterpretableStep = new DefaultStepBehavior();

    public Rcml render(Interpreter interpreter) throws InterpreterException {
        RcmlDialStep rcmlStep = new RcmlDialStep();

        for ( BaseDialNoun noun: dialNouns ) {
            rcmlStep.getNouns().add( ((Interpretable) noun).render(interpreter));
        }

        if ( ! RvdUtils.isEmpty(nextModule) ) {
            String newtarget = interpreter.getTarget().getNodename() + "." + getName() + ".actionhandler";
            Map<String, String> pairs = new HashMap<String, String>();
            pairs.put("target", newtarget);
            String action = interpreter.buildAction(pairs);
            rcmlStep.setAction(action);
            rcmlStep.setAction(method);
        }

        rcmlStep.setTimeout(timeout == null ? null : timeout.toString());
        rcmlStep.setTimeLimit((timeLimit == null ? null : timeLimit.toString()));
        rcmlStep.setCallerId(interpreter.populateVariables(callerId));
        rcmlStep.setRecord( record);

        return rcmlStep;
    }

    @Override
    public void handleAction(Interpreter interpreter, Target originTarget) throws InterpreterException, StorageException {
        LoggingContext logging = interpreter.getRvdContext().logging;
        if (RvdLoggers.local.isEnabledFor(Level.INFO))
            RvdLoggers.local.log(Level.INFO, LoggingHelper.buildMessage(getClass(),"handleAction", logging.getPrefix(), "handling dial action"));
        if ( RvdUtils.isEmpty(nextModule) )
            throw new InterpreterException( "'next' module is not defined for step " + getName() );

        String publicRecordingUrl = interpreter.getRequestParams().getFirst("PublicRecordingUrl");
        if ( publicRecordingUrl != null ) {
            interpreter.getVariables().put(RvdConfiguration.CORE_VARIABLE_PREFIX + "PublicRecordingUrl", publicRecordingUrl);
        }

        String restcommRecordingUrl = interpreter.getRequestParams().getFirst("RecordingUrl");
        if ( restcommRecordingUrl != null ) {
            try {
                String recordingUrl = interpreter.convertRecordingFileResourceHttp(restcommRecordingUrl, interpreter.getHttpRequest());
                interpreter.getVariables().put(RvdConfiguration.CORE_VARIABLE_PREFIX + "RecordingUrl", recordingUrl);
            } catch (URISyntaxException e) {
                RvdLoggers.local.log(Level.WARN, LoggingHelper.buildMessage(getClass(),"handleAction", logging.getPrefix(), "cannot convert file URL to http URL - " + restcommRecordingUrl), e);
            }
        }

        String DialCallStatus = interpreter.getRequestParams().getFirst("DialCallStatus");
        if ( DialCallStatus != null )
            interpreter.getVariables().put(RvdConfiguration.CORE_VARIABLE_PREFIX + "DialCallStatus", DialCallStatus);

        String DialCallSid = interpreter.getRequestParams().getFirst("DialCallSid");
        if ( DialCallSid != null )
            interpreter.getVariables().put(RvdConfiguration.CORE_VARIABLE_PREFIX + "DialCallSid", DialCallSid);

        String DialCallDuration = interpreter.getRequestParams().getFirst("DialCallDuration");
        if ( DialCallDuration != null )
            interpreter.getVariables().put(RvdConfiguration.CORE_VARIABLE_PREFIX + "DialCallDuration", DialCallDuration);

        String DialRingDuration = interpreter.getRequestParams().getFirst("DialRingDuration");
        if ( DialRingDuration != null )
            interpreter.getVariables().put(RvdConfiguration.CORE_VARIABLE_PREFIX + "DialRingDuration", DialRingDuration);

        interpreter.interpret( nextModule, null, null, originTarget );
    }

    @Override
    public String process(Interpreter interpreter, HttpServletRequest httpRequest) throws InterpreterException {
        return defaultInterpretableStep.process(interpreter,httpRequest);
    }

}

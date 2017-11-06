package org.restcomm.connect.rvd.model.steps.dial;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Level;

import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.logging.system.LoggingHelper;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.project.Node;
import org.restcomm.connect.rvd.utils.RvdUtils;
import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.model.project.Step;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

public class DialStep extends Step {

    List<DialNoun> dialNouns = new ArrayList<DialNoun>();
    private String action;
    private String method;
    private Integer timeout;
    private Integer timeLimit;
    private String callerId;
    private String nextModule;
    private Boolean record;

    public RcmlDialStep render(Interpreter interpreter, String containerModule) throws InterpreterException {
        RcmlDialStep rcmlStep = new RcmlDialStep();

        for ( DialNoun noun: dialNouns ) {
            rcmlStep.nouns.add( noun.render(interpreter) );
        }

        if ( ! RvdUtils.isEmpty(nextModule) ) {
            String newtarget = containerModule + "." + getName() + ".actionhandler";
            Map<String, String> pairs = new HashMap<String, String>();
            pairs.put("target", newtarget);
            String action = interpreter.buildAction(pairs);
            rcmlStep.action = action;
            rcmlStep.method = method;
        }

        rcmlStep.timeout = timeout == null ? null : timeout.toString();
        rcmlStep.timeLimit = (timeLimit == null ? null : timeLimit.toString());
        rcmlStep.callerId = interpreter.populateVariables(callerId);
        rcmlStep.record = record;

        return rcmlStep;
    }

    @Override
    public void handleAction(Interpreter interpreter, Node handlerModule) throws InterpreterException, StorageException {
        LoggingContext logging = interpreter.getLoggingContext();
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
                String recordingUrl = interpreter.convertRecordingFileResourceHttp(restcommRecordingUrl);
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

        interpreter.interpret( nextModule, null, null, handlerModule);
    }

}

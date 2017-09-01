package org.restcomm.connect.rvd.interpreter.steps;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Level;
import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.DefaultStepBehavior;
import org.restcomm.connect.rvd.interpreter.InterpretableStep;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.interpreter.Target;
import org.restcomm.connect.rvd.interpreter.rcml.Rcml;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.logging.system.LoggingHelper;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.steps.play.PlayStep;
import org.restcomm.connect.rvd.interpreter.rcml.RcmlPlayStep;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class InterpretedPlayStep extends PlayStep implements InterpretableStep {

    static InterpretableStep defaultInterpretableStep = new DefaultStepBehavior();

    @Override
    public Rcml render(Interpreter interpreter) {
        LoggingContext logging = interpreter.getRvdContext().logging;
        RcmlPlayStep playStep = new RcmlPlayStep();
        String url = "";
        if ("local".equals(playType)) {
            String rawurl = interpreter.getContextPath() + "/services/projects/" + interpreter.getAppName() + "/wavs/" + local.wavLocalFilename;
            try {
                URIBuilder uribuilder = new URIBuilder();
                uribuilder.setPath(rawurl);
                url = uribuilder.build().toString();
            } catch (URISyntaxException e) {

                RvdLoggers.local.log(Level.WARN, LoggingHelper.buildMessage(getClass(),"render", logging.getPrefix(), "error parsing url for play verb: " + rawurl), e);
                url = rawurl; // best effort
            }
        }
        else {
            url = interpreter.populateVariables(remote.wavUrl);
        }

        if (RvdLoggers.local.isTraceEnabled())
            RvdLoggers.local.log(Level.TRACE, LoggingHelper.buildMessage(getClass(),"render","{0} play url: {1}", new Object[] {logging.getPrefix(),url}));

        playStep.setWavurl(url);
        playStep.setLoop(loop);

        return playStep;
    }

    @Override
    public void handleAction(Interpreter interpreter, Target originTarget) throws InterpreterException, StorageException {
        defaultInterpretableStep.handleAction(interpreter, originTarget);
    }

    @Override
    public String process(Interpreter interpreter, HttpServletRequest httpRequest) throws InterpreterException {
        return defaultInterpretableStep.process(interpreter,httpRequest);
    }
}

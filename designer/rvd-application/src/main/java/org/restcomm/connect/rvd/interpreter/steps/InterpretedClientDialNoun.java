package org.restcomm.connect.rvd.interpreter.steps;

import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.Interpretable;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.interpreter.rcml.Rcml;
import org.restcomm.connect.rvd.model.steps.dial.ClientDialNoun;
import org.restcomm.connect.rvd.interpreter.rcml.RcmlClientNoun;
import org.restcomm.connect.rvd.utils.RvdUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class InterpretedClientDialNoun extends ClientDialNoun implements Interpretable {

    @Override
    public Rcml render(Interpreter interpreter) throws InterpreterException {
        RcmlClientNoun rcmlNoun = new RcmlClientNoun();

        if ( ! RvdUtils.isEmpty(getBeforeConnectModule()) ) {
            Map<String, String> pairs = new HashMap<String, String>();
            pairs.put("target", getBeforeConnectModule());
            rcmlNoun.setUrl( interpreter.buildAction(pairs) );
        }

        rcmlNoun.setDestination( interpreter.populateVariables(getDestination()) );
        if  (! RvdUtils.isEmpty(statusCallback))
            rcmlNoun.setStatusCallback(statusCallback);
        else
        if (!RvdUtils.isEmpty(statusCallbackModule)) {
            Map<String, String> pairs = new HashMap<String, String>();
            pairs.put("target", statusCallbackModule);
            rcmlNoun.setStatusCallback(interpreter.buildAction(pairs));
        }
        // populate video attributes (only if video is supported by configuration)
        if (interpreter.getConfiguration().getVideoSupport() && (this.enableVideo != null && this.enableVideo)) {
            rcmlNoun.setVideo (new RcmlClientNoun.Video());
            rcmlNoun.getVideo().enable = this.enableVideo;
            rcmlNoun.getVideo().overlay = this.videoOverlay;
        }

        return rcmlNoun;
    }

}

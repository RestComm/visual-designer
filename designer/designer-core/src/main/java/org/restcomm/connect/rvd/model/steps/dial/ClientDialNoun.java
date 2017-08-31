package org.restcomm.connect.rvd.model.steps.dial;

import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.utils.RvdUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Orestis Tsakiridis - otsakir@gmail.com
 */
public class ClientDialNoun extends DialNoun {
    private String destination;
    private String beforeConnectModule;
    private String statusCallback;
    private String statusCallbackModule;
    private Boolean enableVideo;
    private String videoOverlay;


    public String getDestination() {
        return destination;
    }
    public String getBeforeConnectModule() {
        return beforeConnectModule;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public RcmlNoun render(Interpreter interpreter) throws InterpreterException {
        RcmlClientNoun rcmlNoun = new RcmlClientNoun();

        if ( ! RvdUtils.isEmpty(getBeforeConnectModule()) ) {
            Map<String, String> pairs = new HashMap<String, String>();
            pairs.put("target", getBeforeConnectModule());
            rcmlNoun.setUrl( interpreter.buildAction(pairs) );
        }

        rcmlNoun.setDestination( interpreter.populateVariables(getDestination()) );
        if  (! RvdUtils.isEmpty(statusCallback))
            rcmlNoun.statusCallback = statusCallback;
        else
        if (!RvdUtils.isEmpty(statusCallbackModule)) {
            Map<String, String> pairs = new HashMap<String, String>();
            pairs.put("target", statusCallbackModule);
            rcmlNoun.statusCallback = interpreter.buildAction(pairs);
        }
        // populate video attributes (only if video is supported by configuration)
        if (interpreter.getConfiguration().getVideoSupport() && (this.enableVideo != null && this.enableVideo)) {
            rcmlNoun.video = new RcmlClientNoun.Video();
            rcmlNoun.video.enable = this.enableVideo;
            rcmlNoun.video.overlay = this.videoOverlay;
        }

        return rcmlNoun;
    }

    public Boolean getEnableVideo() {
        return enableVideo;
    }

    public String getVideoOverlay() {
        return videoOverlay;
    }
}

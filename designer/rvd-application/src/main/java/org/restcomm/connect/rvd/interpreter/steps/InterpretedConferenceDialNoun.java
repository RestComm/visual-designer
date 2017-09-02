package org.restcomm.connect.rvd.interpreter.steps;

import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.Interpretable;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.interpreter.rcml.Rcml;
import org.restcomm.connect.rvd.interpreter.rcml.RcmlConferenceNoun;
import org.restcomm.connect.rvd.model.steps.dial.ConferenceDialNoun;
import org.restcomm.connect.rvd.utils.RvdUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class InterpretedConferenceDialNoun extends ConferenceDialNoun implements Interpretable {

    @Override
    public Rcml render(Interpreter interpreter) throws InterpreterException {
        RcmlConferenceNoun rcmlNoun = new RcmlConferenceNoun();

        // set waitUrl
        if ( ! RvdUtils.isEmpty(getWaitModule()) ) {
            Map<String, String> pairs = new HashMap<String, String>();
            pairs.put("target", getWaitModule());
            String action = interpreter.buildAction(pairs);
            rcmlNoun.setWaitUrl( interpreter.getConfiguration().getApplicationsRelativeUrl() + "/" + interpreter.getAppName() + "/" + action  );
        } else
        if ( ! RvdUtils.isEmpty(getWaitUrl())) {
            rcmlNoun.setWaitUrl(interpreter.populateVariables(getWaitUrl()));
        }

        rcmlNoun.setBeep(getBeep());
        rcmlNoun.setMuted(getMuted());
        rcmlNoun.setEndConferenceOnExit(getEndConferenceOnExit());
        rcmlNoun.setStartConferenceOnEnter(getStartConferenceOnEnter());
        rcmlNoun.setMaxParticipants(getMaxParticipants());
        rcmlNoun.setWaitMethod(getWaitMethod());
        rcmlNoun.setDestination( interpreter.populateVariables(getDestination() ));
        if (!RvdUtils.isEmpty(getStatusCallback()))
            rcmlNoun.setStatusCallback(getStatusCallback());
        else
        if (!RvdUtils.isEmpty(getStatusCallbackModule())) {
            Map<String, String> pairs = new HashMap<String, String>();
            pairs.put("target", getStatusCallbackModule());
            rcmlNoun.setStatusCallback( interpreter.buildAction(pairs));
        }
        // populate video attributes (only if video is supported by configuration)
        if (interpreter.getConfiguration().getVideoSupport() && (this.getEnableVideo() != null && this.getEnableVideo())) {
            rcmlNoun.setVideo( new RcmlConferenceNoun.Video());
            rcmlNoun.getVideo().enable = this.getEnableVideo();
            if (this.getVideoMode() != null)
                rcmlNoun.getVideo().mode = this.getVideoMode().toString();
            rcmlNoun.getVideo().resolution = this.getVideoResolution();
            if (this.getVideoLayout() != null)
                rcmlNoun.getVideo().layout = this.getVideoLayout().toString();
            rcmlNoun.getVideo().overlay = this.getVideoOverlay();
        }

        return rcmlNoun;
    }}

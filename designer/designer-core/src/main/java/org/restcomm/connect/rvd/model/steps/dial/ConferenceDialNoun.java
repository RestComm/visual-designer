package org.restcomm.connect.rvd.model.steps.dial;

import java.util.HashMap;
import java.util.Map;

import org.restcomm.connect.rvd.utils.RvdUtils;
import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.Interpreter;

public class ConferenceDialNoun extends DialNoun {

    public enum VideoMode {
        mcu, sfu
    }
    public enum VideoLayout {
        linear, tile
    }

    private String destination;
    private Boolean muted;
    private Boolean beep;
    private Boolean startConferenceOnEnter;
    private Boolean endConferenceOnExit;
    private String waitUrl;
    private String waitMethod;
    private String waitModule;
    private Integer maxParticipants;
    private String nextModule;
    private String statusCallback;
    private String statusCallbackModule;
    private Boolean enableVideo;
    private VideoMode videoMode;
    private String videoResolution; // can't use an enum here since some constants start with a number
    private VideoLayout videoLayout;
    private String videoOverlay;

    public String getWaitMethod() {
        return waitMethod;
    }
    public void setWaitMethod(String waitMethod) {
        this.waitMethod = waitMethod;
    }
    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }
    public String getNextModule() {
        return nextModule;
    }
    public void setNextModule(String nextModule) {
        this.nextModule = nextModule;
    }
    public Boolean getMuted() {
        return muted;
    }
    public void setMuted(Boolean muted) {
        this.muted = muted;
    }
    public Boolean getBeep() {
        return beep;
    }
    public void setBeep(Boolean beep) {
        this.beep = beep;
    }
    public Boolean getStartConferenceOnEnter() {
        return startConferenceOnEnter;
    }
    public void setStartConferenceOnEnter(Boolean startConferenceOnEnter) {
        this.startConferenceOnEnter = startConferenceOnEnter;
    }
    public Boolean getEndConferenceOnExit() {
        return endConferenceOnExit;
    }
    public void setEndConferenceOnExit(Boolean endConferenceOnExit) {
        this.endConferenceOnExit = endConferenceOnExit;
    }
    public String getWaitUrl() {
        return waitUrl;
    }
    public void setWaitUrl(String waitUrl) {
        this.waitUrl = waitUrl;
    }
    public String getWaitModule() {
        return waitModule;
    }
    public void setWaitModule(String waitModule) {
        this.waitModule = waitModule;
    }
    public Integer getMaxParticipants() {
        return maxParticipants;
    }
    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Boolean getEnableVideo() {
        return enableVideo;
    }

    public VideoMode getVideoMode() {
        return videoMode;
    }

    public String getVideoResolution() {
        return videoResolution;
    }

    public VideoLayout getVideoLayout() {
        return videoLayout;
    }

    public String getVideoOverlay() {
        return videoOverlay;
    }

    @Override
    public RcmlNoun render(Interpreter interpreter) throws InterpreterException {
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
        if (!RvdUtils.isEmpty(statusCallback))
            rcmlNoun.statusCallback = statusCallback;
        else
        if (!RvdUtils.isEmpty(statusCallbackModule)) {
            Map<String, String> pairs = new HashMap<String, String>();
            pairs.put("target", statusCallbackModule);
            rcmlNoun.statusCallback = interpreter.buildAction(pairs);
        }
        // populate video attributes (only if video is supported by configuration)
        if (interpreter.getConfiguration().getVideoSupport() && (this.enableVideo != null && this.enableVideo)) {
            rcmlNoun.video = new RcmlConferenceNoun.Video();
            rcmlNoun.video.enable = this.enableVideo;
            if (this.videoMode != null)
                rcmlNoun.video.mode = this.videoMode.toString();
            rcmlNoun.video.resolution = this.videoResolution;
            if (this.videoLayout != null)
                rcmlNoun.video.layout = this.videoLayout.toString();
            rcmlNoun.video.overlay = this.videoOverlay;
        }

        return rcmlNoun;
    }
}

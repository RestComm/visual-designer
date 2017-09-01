package org.restcomm.connect.rvd.model.steps.dial;


public class ConferenceDialNoun extends BaseDialNoun {

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

    public String getStatusCallback() {
        return statusCallback;
    }

    public void setStatusCallback(String statusCallback) {
        this.statusCallback = statusCallback;
    }

    public String getStatusCallbackModule() {
        return statusCallbackModule;
    }

    public void setStatusCallbackModule(String statusCallbackModule) {
        this.statusCallbackModule = statusCallbackModule;
    }

    public void setEnableVideo(Boolean enableVideo) {
        this.enableVideo = enableVideo;
    }

    public void setVideoMode(VideoMode videoMode) {
        this.videoMode = videoMode;
    }

    public void setVideoResolution(String videoResolution) {
        this.videoResolution = videoResolution;
    }

    public void setVideoLayout(VideoLayout videoLayout) {
        this.videoLayout = videoLayout;
    }

    public void setVideoOverlay(String videoOverlay) {
        this.videoOverlay = videoOverlay;
    }
}

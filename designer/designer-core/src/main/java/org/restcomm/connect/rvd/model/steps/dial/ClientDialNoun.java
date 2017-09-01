package org.restcomm.connect.rvd.model.steps.dial;

/**
 * @author Orestis Tsakiridis - otsakir@gmail.com
 */
public class ClientDialNoun extends BaseDialNoun {
    protected String destination;
    protected  String beforeConnectModule;
    protected  String statusCallback;
    protected  String statusCallbackModule;
    protected  Boolean enableVideo;
    protected  String videoOverlay;


    public String getDestination() {
        return destination;
    }
    public String getBeforeConnectModule() {
        return beforeConnectModule;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }


    public Boolean getEnableVideo() {
        return enableVideo;
    }

    public String getVideoOverlay() {
        return videoOverlay;
    }
}

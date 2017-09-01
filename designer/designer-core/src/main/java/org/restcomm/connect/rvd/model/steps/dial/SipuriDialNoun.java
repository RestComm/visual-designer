package org.restcomm.connect.rvd.model.steps.dial;

/**
 * @author Orestis Tsakiridis - otsakir@gmail.com
 */
public class SipuriDialNoun extends BaseDialNoun {
    protected String destination;
    protected String statusCallback;
    protected String statusCallbackModule;
    protected Boolean enableVideo;
    protected String videoOverlay;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

}

package org.restcomm.connect.rvd.model.steps.dial;

/**
 * @author Orestis Tsakiridis - otsakir@gmail.com
 */
public class NumberDialNoun extends BaseDialNoun {

    private String destination;
    private String beforeConnectModule;
    private String sendDigits;
    private String statusCallback;
    private String statusCallbackModule;


    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }
    public String getBeforeConnectModule() {
        return beforeConnectModule;
    }
    public void setBeforeConnectModule(String beforeConnectModule) {
        this.beforeConnectModule = beforeConnectModule;
    }
    public String getSendDigits() {
        return sendDigits;
    }
    public void setSendDigits(String sendDigits) {
        this.sendDigits = sendDigits;
    }

    public void setStatusCallback(String statusCallback) {
        this.statusCallback = statusCallback;
    }

    public void setStatusCallbackModule(String statusCallbackModule) {
        this.statusCallbackModule = statusCallbackModule;
    }

    public String getStatusCallback() {
        return statusCallback;
    }

    public String getStatusCallbackModule() {
        return statusCallbackModule;
    }
}

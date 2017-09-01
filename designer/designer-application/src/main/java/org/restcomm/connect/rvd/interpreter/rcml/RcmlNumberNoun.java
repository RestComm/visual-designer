package org.restcomm.connect.rvd.interpreter.rcml;


public class RcmlNumberNoun extends Rcml {
    String sendDigits;
    String url;
    String destination;
    String statusCallback;

    public String getSendDigits() {
        return sendDigits;
    }
    public void setSendDigits(String sendDigits) {
        this.sendDigits = sendDigits;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setStatusCallback(String statusCallback) {
        this.statusCallback = statusCallback;
    }
}

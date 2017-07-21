package org.restcomm.connect.rvd.model.steps.sms;

import org.restcomm.connect.rvd.model.rcml.RcmlStep;

public class RcmlSmsStep extends RcmlStep {
    String text;
    String from;
    String to;
    String action;
    String method;
    String statusCallback;
    String encoding;
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public String getStatusCallback() {
        return statusCallback;
    }
    public void setStatusCallback(String statusCallback) {
        this.statusCallback = statusCallback;
    }
    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}

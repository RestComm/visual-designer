package org.restcomm.connect.rvd.interpreter.rcml;

import java.util.ArrayList;
import java.util.List;

public class RcmlDialStep extends Rcml {
    List<Rcml> nouns = new ArrayList<Rcml>();
    String action;
    String method;
    String timeout;
    String timeLimit;
    String callerId;
    Boolean record;

    public List<Rcml> getNouns() {
        return nouns;
    }

    public String getAction() {
        return action;
    }

    public String getMethod() {
        return method;
    }

    public String getTimeout() {
        return timeout;
    }

    public String getTimeLimit() {
        return timeLimit;
    }

    public String getCallerId() {
        return callerId;
    }

    public Boolean getRecord() {
        return record;
    }

    public void setNouns(List<Rcml> nouns) {
        this.nouns = nouns;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public void setRecord(Boolean record) {
        this.record = record;
    }
}

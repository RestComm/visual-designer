package org.restcomm.connect.rvd.model.steps.gather;

import java.util.ArrayList;
import java.util.List;

import org.restcomm.connect.rvd.model.rcml.RcmlStep;

public class RcmlGatherStep extends RcmlStep {
    private String action;
    private String method;
    private Integer timeout;
    private String finishOnKey;
    private Integer numDigits;

    private String input;
    private String language;
    private String hints;
    private String partialResultCallback;
    private String partialResultCallbackMethod;

    private List<RcmlStep> steps = new ArrayList<RcmlStep>();

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<RcmlStep> getSteps() {
        return steps;
    }

    public void setSteps(List<RcmlStep> steps) {
        this.steps = steps;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getFinishOnKey() {
        return finishOnKey;
    }

    public void setFinishOnKey(String finishOnKey) {
        this.finishOnKey = finishOnKey;
    }

    public Integer getNumDigits() {
        return numDigits;
    }

    public void setNumDigits(Integer numDigits) {
        this.numDigits = numDigits;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getHints() {
        return hints;
    }

    public void setHints(String hints) {
        this.hints = hints;
    }

    public String getPartialResultCallback() {
        return partialResultCallback;
    }

    public void setPartialResultCallback(String partialResultCallback) {
        this.partialResultCallback = partialResultCallback;
    }

    public String getPartialResultCallbackMethod() {
        return partialResultCallbackMethod;
    }

    public void setPartialResultCallbackMethod(String partialResultCallbackMethod) {
        this.partialResultCallbackMethod = partialResultCallbackMethod;
    }
}

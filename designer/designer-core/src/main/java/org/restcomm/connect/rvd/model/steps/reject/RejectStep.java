package org.restcomm.connect.rvd.model.steps.reject;

import org.restcomm.connect.rvd.model.project.BaseStep;

public class RejectStep extends BaseStep {
    String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}

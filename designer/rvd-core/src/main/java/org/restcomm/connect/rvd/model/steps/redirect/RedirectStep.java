package org.restcomm.connect.rvd.model.steps.redirect;

import org.restcomm.connect.rvd.model.project.BaseStep;

public class RedirectStep extends BaseStep {
    String url;
    String method;

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

}

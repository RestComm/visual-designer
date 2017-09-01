package org.restcomm.connect.rvd.model.steps.es;

import java.util.List;

import org.restcomm.connect.rvd.model.project.BaseStep;

public class ExternalServiceStep extends BaseStep {
    public static final String CONTENT_TYPE_WWWFORM = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_JSON = "application/json";

    protected String url; // supports RVD variable expansion when executing the HTTP request
    protected String method;
    protected String username;
    protected String password;
    protected List<EsUrlParam> urlParams;
    protected String contentType;
    protected String requestBody;
    protected Boolean populatePostBodyFromParams;
    protected List<Assignment> assignments;
    protected String next;
    protected String nextVariable;
    protected Boolean doRouting;
    protected String nextType;
    protected ValueExtractor nextValueExtractor;
    protected List<RouteMapping> routeMappings;
    //private String defaultNext;
    protected String exceptionNext;
    protected Integer timeout; // timeout in milliseconds
    protected String onTimeout;


    public ValueExtractor getNextValueExtractor() {
        return nextValueExtractor;
    }

    public void setNextValueExtractor(ValueExtractor nextValueExtractor) {
        this.nextValueExtractor = nextValueExtractor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public List<RouteMapping> getRouteMappings() {
        return routeMappings;
    }

    public void setRouteMappings(List<RouteMapping> routeMappings) {
        this.routeMappings = routeMappings;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public Boolean getDoRouting() {
        return doRouting;
    }

    public void setDoRouting(Boolean doRouting) {
        this.doRouting = doRouting;
    }

    public String getNextType() {
        return nextType;
    }

    public void setNextType(String nextType) {
        this.nextType = nextType;
    }

    public String getNextVariable() {
        return nextVariable;
    }

    public void setNextVariable(String nextVariable) {
        this.nextVariable = nextVariable;
    }
    public List<EsUrlParam> getUrlParams() {
        return this.urlParams;
    }

    public String getMethod() {
        return method;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    public String getExceptionNext() {
        return exceptionNext;
    }

    public void setExceptionNext(String exceptionNext) {
        this.exceptionNext = exceptionNext;
    }

    public String getOnTimeout() {
        return onTimeout;
    }

    public Integer getTimeout() {
        return timeout;
    }

}

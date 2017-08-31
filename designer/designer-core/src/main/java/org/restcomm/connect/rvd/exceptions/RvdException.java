package org.restcomm.connect.rvd.exceptions;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class RvdException extends Exception {

    String accountSid;  // account of the user that caused the exception if available
    String applicationSid; // application related to the exception
    String callSid; // callSid if available

    public RvdException() {
        super();
    }

    public RvdException(String message, Throwable cause) {
        super(message, cause);
    }

    public RvdException(String message) {
        super(message);
    }

    public RvdException setAccountSid(String accountSid) {
        this.accountSid = accountSid;
        return this;
    }

    public RvdException setApplicationSid(String applicationSid) {
        this.applicationSid = applicationSid;
        return this;
    }

    public String getAccountSid() {
        return accountSid;
    }

    public String getApplicationSid() {
        return applicationSid;
    }

    public String getCallSid() {
        return callSid;
    }

    public void setCallSid(String callSid) {
        this.callSid = callSid;
    }

    public ExceptionResult getExceptionSummary() {
        return new ExceptionResult(getClass().getSimpleName(), getMessage());
    }

    public String asJson() {
        Gson gson = new Gson();
        JsonObject errorResponse = new JsonObject();
        ExceptionResult result = new ExceptionResult(getClass().getSimpleName(), getMessage());
        errorResponse.add("serverError", gson.toJsonTree(result));
        return gson.toJson(errorResponse);
    }

}

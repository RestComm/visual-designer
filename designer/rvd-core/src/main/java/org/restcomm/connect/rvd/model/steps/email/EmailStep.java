package org.restcomm.connect.rvd.model.steps.email;

import org.restcomm.connect.rvd.model.project.BaseStep;


/**
 * Created by lefty on 6/24/15.
 */
public class EmailStep extends BaseStep {

    String text;
    String to;
    String from;
    String bcc;
    String cc;
    String subject;
    String statusCallback;
    String method;
    String next;

    public String getNext() {
        return next;
    }
    public void setNext(String next) {
        this.next = next;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
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
    public String getCc() {
             return cc;
           }
    public void setCc(String cc) {
             this.cc = cc;
       }
    public String getBcc() {
         return bcc;
          }
    public void setBcc(String bcc) {
             this.bcc = bcc;
         }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String Subject) {
        this.subject = Subject;
    }
    public String getStatusCallback() {
        return statusCallback;
    }
    public void setStatusCallback(String statusCallback) {
        this.statusCallback = statusCallback;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

}
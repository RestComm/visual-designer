/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.restcomm.connect.rvd.model.steps.sms;

import org.restcomm.connect.rvd.model.project.BaseStep;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class SmsStep extends BaseStep {
    String text;
    String to;
    String from;
    String statusCallback;
    String method;
    String next;

    /*
    public static SmsStep createDefault(String name, String phrase) {
        SmsStep step = new SmsStep();
        step.setName(name);
        step.setLabel("sms");
        step.setKind("sms");
        step.setTitle("sms");
        step.setText(phrase);

        return step;
    }
    */

    // TODO - add the default no-param constructor here ?

    public SmsStep(String phrase) {
        setLabel("sms");
        setKind("sms");
        setTitle("sms");
        setText(phrase);
    }

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
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getStatusCallback() {
        return statusCallback;
    }
    public void setStatusCallback(String statusCallback) {
        this.statusCallback = statusCallback;
    }

}

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

package org.restcomm.connect.rvd.model.steps.record;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.utils.RvdUtils;
import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.interpreter.Target;
import org.restcomm.connect.rvd.model.client.Step;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class RecordStep extends Step {
    String next;
    String method;
    Integer timeout;
    String finishOnKey;
    Integer maxLength;
    Boolean transcribe;
    String transcribeCallback;
    public String getTranscribeCallback() {
        return transcribeCallback;
    }
    public void setTranscribeCallback(String transcribeCallback) {
        this.transcribeCallback = transcribeCallback;
    }
    Boolean playBeep;
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
    public Integer getMaxLength() {
        return maxLength;
    }
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }
    public Boolean getTranscribe() {
        return transcribe;
    }
    public void setTranscribe(Boolean transcribe) {
        this.transcribe = transcribe;
    }
    public Boolean getPlayBeep() {
        return playBeep;
    }
    public void setPlayBeep(Boolean playBeep) {
        this.playBeep = playBeep;
    }
    public RcmlRecordStep render(Interpreter interpreter) {
        RcmlRecordStep rcmlStep = new RcmlRecordStep();

        if ( ! RvdUtils.isEmpty(getNext()) ) {
            String newtarget = interpreter.getTarget().getNodename() + "." + getName() + ".actionhandler";
            Map<String, String> pairs = new HashMap<String, String>();
            pairs.put("target", newtarget);
            String action = interpreter.buildAction(pairs);
            rcmlStep.setAction(action);
            rcmlStep.setMethod(getMethod());
        }

        rcmlStep.setFinishOnKey(getFinishOnKey());
        rcmlStep.setMaxLength(getMaxLength());
        rcmlStep.setPlayBeep(getPlayBeep());
        rcmlStep.setTimeout(getTimeout());
        rcmlStep.setTranscribe(getTranscribe());
        rcmlStep.setTranscribeCallback(getTranscribeCallback());

        return rcmlStep;
    }

    @Override
    public void handleAction(Interpreter interpreter, Target originTarget) throws InterpreterException, StorageException {
        LoggingContext logging = interpreter.getRvdContext().logging;
        if (logging.system.isLoggable(Level.INFO))
            logging.system.log(Level.INFO, logging.getPrefix() + "handling record action");

        if ( RvdUtils.isEmpty(getNext()) )
            throw new InterpreterException( "'next' module is not defined for step " + getName() );

        String publicRecordingUrl = interpreter.getRequestParams().getFirst("PublicRecordingUrl");
        if ( publicRecordingUrl != null ) {
            interpreter.getVariables().put(RvdConfiguration.CORE_VARIABLE_PREFIX + "PublicRecordingUrl", publicRecordingUrl);
        }

        String restcommRecordingUrl = interpreter.getRequestParams().getFirst("RecordingUrl");
        if ( restcommRecordingUrl != null ) {
            try {
                String recordingUrl = interpreter.convertRecordingFileResourceHttp(restcommRecordingUrl, interpreter.getHttpRequest());
                interpreter.getVariables().put(RvdConfiguration.CORE_VARIABLE_PREFIX + "RecordingUrl", recordingUrl);
            } catch (URISyntaxException e) {
                if (logging.system.isLoggable(Level.WARNING))
                    logging.system.log(Level.WARNING, "{0} cannot convert file URL to http URL - {1}", new Object[] {logging.getPrefix(), restcommRecordingUrl});
            }
        }

        String RecordingDuration = interpreter.getRequestParams().getFirst("RecordingDuration");
        if (RecordingDuration != null )
            interpreter.getVariables().put(RvdConfiguration.CORE_VARIABLE_PREFIX + "RecordingDuration", RecordingDuration);

        String Digits = interpreter.getRequestParams().getFirst("Digits");
        if (Digits != null )
            interpreter.getVariables().put(RvdConfiguration.CORE_VARIABLE_PREFIX + "Digits", Digits);

        interpreter.interpret( getNext(), null, null, originTarget );
    }
}

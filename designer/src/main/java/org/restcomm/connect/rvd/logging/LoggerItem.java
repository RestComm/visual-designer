package org.restcomm.connect.rvd.logging;

import java.util.Date;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class LoggerItem {
    public enum LogMarshalling {
        USE_MARSHALLER

    }
    StringBuffer  buffer = new StringBuffer();
    CustomLogger projectLogger;

    LoggerItem(CustomLogger projectLogger) {
        Date date = new Date();
        buffer.append("[" + date.toString() + "]");
    }

    public LoggerItem message(Object payload){
        message(projectLogger.marshaler.toData(payload));
        return this;
    }

    public LoggerItem messageNoMarshalling(Object payload) {
        buffer.append(payload).append(" ");
        return this;
    }

    public LoggerItem tag(String tag) {
        buffer.append("[").append(tag).append("] ");
        return this;
    }

    public LoggerItem tag(String tag, String value) {
        if ( value == null ) {
            tag(tag);
        } else {
            buffer.append("[").append(tag).append(" ").append(value).append("]");
        }
        return this;
    }

    public void done() {
        projectLogger.done(this);
    }

    StringBuffer getBuffer() {
        return buffer;
    }

}

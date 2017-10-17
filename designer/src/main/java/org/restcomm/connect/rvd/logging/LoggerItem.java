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
        this.projectLogger = projectLogger;
        Date date = new Date();
        buffer.append("[" + date.toString() + "]");
    }

    /**
     * Build a log message using a marchaller if available in the projectLogger
     *
     * @param payload
     * @return
     */
    public LoggerItem message(Object payload){
        if (projectLogger.marshaler != null)
            messageNoMarshalling(projectLogger.marshaler.toData(payload));
        else
            messageNoMarshalling(payload);
        return this;
    }

    public LoggerItem messageNoMarshalling(Object payload) {
        buffer.append(" ").append(payload);
        return this;
    }

    public LoggerItem tag(String tag) {
        buffer.append(" [").append(tag).append("] ");
        return this;
    }

    public LoggerItem tag(String tag, String value) {
        if ( value == null ) {
            tag(tag);
        } else {
            buffer.append(" [").append(tag).append(" ").append(value).append("]");
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

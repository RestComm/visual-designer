package org.restcomm.connect.rvd.logging.system;

import java.util.logging.Logger;

/**
 * Encapsulates information that should be added to the logging statements like prefix/suffix etc.
 * A single LoggingContext is created per request.
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class LoggingContext {
    private String prefix;
    public Logger system;
    public Logger global;


    public LoggingContext(String prefix) {
        this.prefix = prefix;
    }

    public LoggingContext() {
        prefix = "";
    }

    public String getPrefix() {
        return prefix;
    }

    public void appendPrefix(String word) {
        this.prefix += word;
    }

    public void appendApplicationSid(String sid) {
        if (sid != null)
            this.prefix += "["+sid.substring(0, 16)+"] ";
    }
}

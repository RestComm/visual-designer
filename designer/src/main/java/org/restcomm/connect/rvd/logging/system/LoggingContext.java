package org.restcomm.connect.rvd.logging.system;


/**
 * Encapsulates information that should be added to the logging statements like prefix/suffix etc.
 * A single LoggingContext is created per request.
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class LoggingContext {
    private StringBuffer prefix;

    public LoggingContext(String prefix) {
        this.prefix = new StringBuffer(prefix);
    }

    public LoggingContext() {
        this.prefix = new StringBuffer("");
    }

    public String getPrefix() {
        return prefix.toString() + " ";
    }

    public void appendPrefix(String word) {
        prefix.append(word);
    }

    public void appendApplicationSid(String sid) {
        appendPrefix(buildApplicationSid(sid));
    }

    public void appendAccountSid(String sid) {
        appendPrefix(buildAccountSid(sid));
    }

    public static String buildApplicationSid(String sid) {
        if (sid != null)
            return "["+sid.substring(0, 16)+"]";
        return "";
    }

    public static String buildAccountSid(String sid) {
        if (sid != null)
            return "["+sid.substring(0, 16)+"]";
        return "";
    }

    public static String buildPrefix(String accountSid, String applicationSid) {
        return buildAccountSid(accountSid) + buildApplicationSid(applicationSid) + " ";
    }
}

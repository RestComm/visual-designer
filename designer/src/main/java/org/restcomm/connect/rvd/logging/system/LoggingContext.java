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
        return prefix.toString();
    }

    public StringBuffer getPrefixBuffer() {
        return prefix;
    }

    public void appendPrefix(String word) {
        prefix.append(word);
    }

    public void appendApplicationSid(String sid) {
        appendSid(sid);
    }

    public void appendAccountSid(String sid) {
        appendSid(sid);
    }

    public void appendCallSid(String sid) { appendSid(sid);}

    public void appendSid(String sid) {
        if (sid != null)
            prefix.append("[").append(sid).append("]");
    }

    static String buildApplicationSid(String sid) {
        if (sid != null)
            return "["+sid+"]";
        return "";
    }

    static String buildAccountSid(String sid) {
        if (sid != null)
            return "["+sid+"]";
        return "";
    }

    static String buildCallSid(String sid) {
        if (sid != null)
            return "["+sid+"]";
        return "";
    }

    public static String buildPrefix(String accountSid, String applicationSid, String callSid) {
        return buildAccountSid(accountSid) + buildApplicationSid(applicationSid) + buildCallSid(callSid) + " ";
    }

}

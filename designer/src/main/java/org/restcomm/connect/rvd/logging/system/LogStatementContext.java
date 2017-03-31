package org.restcomm.connect.rvd.logging.system;

/**
 * Encapsulates information that should be added to the logging statements like prefix/suffix etc.
 * A single LogStatementContext is created per request.
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class LogStatementContext {
    private String prefix;

    public LogStatementContext(String prefix) {
        this.prefix = prefix;
    }

    public LogStatementContext() {
        prefix = "";
    }

    public String getPrefix() {
        return prefix;
    }

    public void appendPrefix(String word) {
        this.prefix += word;
    }
}

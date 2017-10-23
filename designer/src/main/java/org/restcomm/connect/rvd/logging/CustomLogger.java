package org.restcomm.connect.rvd.logging;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public interface CustomLogger {
    LoggerItem log();

    String getLogFilePath();

    // clear the log file
    // TODO check this method for concurrency issues
    void reset();
}

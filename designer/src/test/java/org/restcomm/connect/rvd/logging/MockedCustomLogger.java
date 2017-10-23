package org.restcomm.connect.rvd.logging;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class MockedCustomLogger implements CustomLogger {
    @Override
    public LoggerItem log() {
        return new MockedLoggerItem();
    }

    @Override
    public String getLogFilePath() {
        return "";
    }

    @Override
    public void reset() {

    }
}

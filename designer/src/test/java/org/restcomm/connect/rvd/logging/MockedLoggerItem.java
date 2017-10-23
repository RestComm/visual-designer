package org.restcomm.connect.rvd.logging;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class MockedLoggerItem implements LoggerItem {
    StringBuffer buffer = new StringBuffer();

    @Override
    public LoggerItem message(Object payload) {
        return this;
    }

    @Override
    public LoggerItem messageNoMarshalling(Object payload) {
        return this;
    }

    @Override
    public LoggerItem tag(String tag) {
        return this;
    }

    @Override
    public LoggerItem tag(String tag, String value) {
        return this;
    }

    @Override
    public void done() {

    }

    @Override
    public StringBuffer getBuffer() {
        return buffer;
    }
}

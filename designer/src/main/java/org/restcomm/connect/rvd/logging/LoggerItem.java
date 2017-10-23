package org.restcomm.connect.rvd.logging;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public interface LoggerItem {
    LoggerItem message(Object payload);

    LoggerItem messageNoMarshalling(Object payload);

    LoggerItem tag(String tag);

    LoggerItem tag(String tag, String value);

    void done();

    StringBuffer getBuffer();
}

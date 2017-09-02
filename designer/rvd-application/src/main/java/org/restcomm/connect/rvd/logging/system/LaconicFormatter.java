package org.restcomm.connect.rvd.logging.system;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class LaconicFormatter extends Formatter {
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd HH:mm:ss,SSS X");

    @Override
    public String format(LogRecord record) {
        String message = record.getMessage();
        if (record.getParameters() != null)
            message = MessageFormat.format(message, record.getParameters());
        if (record.getThrown() == null)
            return dateFormat.format(new Date(record.getMillis())) + " " + record.getLevel() + " [" + record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf(".")+1) + ":" + record.getSourceMethodName() + "()] " + message + "\n";
        else
            return dateFormat.format(new Date(record.getMillis())) + " " + record.getLevel() + " [" + record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf(".")+1) + ":" + record.getSourceMethodName() + "()] " + message + "\n" + stackTraceToString(record.getThrown()) + "\n";
    }

    private String stackTraceToString(Throwable thrown) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        thrown.printStackTrace(pw);
        return sw.toString();

    }
}

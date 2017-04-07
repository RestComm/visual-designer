package org.restcomm.connect.rvd.logging.system;

import org.apache.log4j.Appender;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.RollingFileAppender;

import java.io.IOException;

import org.apache.log4j.Logger;


/**
 * A static place to host loggers that are accessed programmatically (not necessarily by class name)
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class RvdLoggers {
    static final String RVD_APPENDER = "rvd-appender";

    public static final Logger local = Logger.getLogger("org.restcomm.connect.rvd.DETAIL"); // detailed messages that are reported only to RVD log
    public static final Logger global = Logger.getLogger("org.restcomm.connect.rvd.PUBLIC"); // messages that are reported both to RVD and restcomm logs
    static Appender appender;

    public static void init(String path) {
        // either create the appender or use the existing one
        appender = getRvdAppender(local, path);
        prepareLogger(local,false);
        prepareLogger(global,true);
    }

    public static Appender getRvdAppender(Logger logger, String path) {
        Appender appender = logger.getAppender(RVD_APPENDER);
        if (appender == null) {
            try {
                appender = new RollingFileAppender(createLayout(), path + "/rvd.log", true );
                appender.setName(RVD_APPENDER);
            } catch (IOException e) {
                throw new RuntimeException("Error initializing RVD local logging", e);
            }
        }
        return appender;
    }

    public static Layout createLayout() {
        return new EnhancedPatternLayout("%d{MMdd HH:mm:ss,SSS X} %p (%t) %m %n");
    }

    public static void prepareLogger(Logger logger, boolean additivity) {
        if (logger != null) {
            if (logger.getAppender(RVD_APPENDER) == null)
                logger.addAppender(appender);
            logger.setAdditivity(additivity);
        }
    }

}

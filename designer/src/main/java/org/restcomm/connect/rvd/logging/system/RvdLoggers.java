package org.restcomm.connect.rvd.logging.system;

import org.apache.log4j.Logger;

/**
 * A static place to host loggers that are accessed programmatically (not necessarily by class name)
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class RvdLoggers {
    public static final Logger local = Logger.getLogger("org.restcomm.connect.rvd.LOCAL"); // detailed messages that are reported only to RVD log
    public static final Logger global = Logger.getLogger("org.restcomm.connect.rvd.GLOBAL"); // messages that are reported both to RVD and restcomm logs

    public static void init(String path) {
        // either create the appender or use the existing one
        prepareLogger(local,false);
        prepareLogger(global,true);
    }

    public static void prepareLogger(Logger logger, boolean additivity) {
        if (logger != null) {
            logger.setAdditivity(additivity);
        }
    }

}

package org.restcomm.connect.rvd.logging.system;

import org.restcomm.connect.rvd.RvdConfiguration;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * A static place to host loggers that are accessed programmatically (not necessarily by class name)
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class RvdLoggers {

    public static final Logger system = Logger.getLogger("RVD.controller");
    public static final Logger global = Logger.getLogger("visual-designer");

    public static void init(String path) {
        // create new handler
        Handler handler;
        try {
            handler = new FileHandler(path + "/rvd.log", RvdConfiguration.SYSTEM_LOG_FILE_SIZE,RvdConfiguration.SYSTEM_LOG_FILE_COUNT, true);
        } catch (IOException e) {
            throw new RuntimeException("Error initializing RVD system logging", e);
        }
        handler.setFormatter(new LaconicFormatter());
        // controller handler
        clearLoggerHandlers(system);
        system.addHandler(handler);
        system.setUseParentHandlers(false); // set this to true in order to propagate messages to core Restcomm log
        system.setLevel(RvdConfiguration.SYSTEM_LOG_LEVEL);
        // global handler
        clearLoggerHandlers(global);
        global.addHandler(handler);
        global.setUseParentHandlers(true); // set this to true in order to propagate messages to core Restcomm log
        global.setLevel(RvdConfiguration.SYSTEM_LOG_LEVEL);
    }

    private static void clearLoggerHandlers(Logger logger) {
        for ( Handler anyhandler: logger.getHandlers()) {
            logger.removeHandler(anyhandler);
        }
    }

    private static void closeLoggerHandlers(Logger logger) {
        for (Handler anyhandler: logger.getHandlers()) {
            anyhandler.close();
        }
    }

    public static void destroy() {
        closeLoggerHandlers(system);
        closeLoggerHandlers(global);
    }


}

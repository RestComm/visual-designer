package org.restcomm.connect.rvd.logging.system;

import org.restcomm.connect.rvd.RvdConfiguration;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * A static place to host loggers that are not accessed by package name
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class SystemLoggers {

    public static final Logger designer = Logger.getLogger("RVD.designer");
    public static final Logger controller = Logger.getLogger("RVD.controller");

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
        clearLoggerHandlers(controller);
        controller.addHandler(handler);
        controller.setUseParentHandlers(false); // set this to true in order to propagate messages to core Restcomm log
        controller.setLevel(RvdConfiguration.SYSTEM_LOG_LEVEL);
        // designer handler
        clearLoggerHandlers(designer);
        designer.addHandler(handler);
        designer.setUseParentHandlers(false); // set this to true in order to propagate messages to core Restcomm log
        designer.setLevel(RvdConfiguration.SYSTEM_LOG_LEVEL);
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
        closeLoggerHandlers(controller);
        closeLoggerHandlers(designer);
    }


}

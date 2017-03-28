package org.restcomm.connect.rvd.logging.system;

import org.restcomm.connect.rvd.logging.system.LaconicFormatter;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * A static place to host loggers that are not accessed by package name
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class StaticLoggers {

    public static void init(String path) {
        // clear existing handlers if any
        clearLoggerHandlers(controllerLogger);
        clearLoggerHandlers(designerLogger);
        // now create new handler
        Handler handler;
        try {
            handler = new FileHandler(path + "/rvd.log",1000000,3, true);
        } catch (IOException e) {
            throw new RuntimeException("Error initializing RVD system logging", e);
        }
        handler.setFormatter(new LaconicFormatter());
        controllerLogger.addHandler(handler);
        // set this to true in order to propagate messages to core Restcomm log
        controllerLogger.setUseParentHandlers(false);
        designerLogger.addHandler(handler);
        // set this to true in order to propagate messages to core Restcomm log
        designerLogger.setUseParentHandlers(false);
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
        closeLoggerHandlers(controllerLogger);
        closeLoggerHandlers(designerLogger);
    }

    public static final Logger designerLogger = Logger.getLogger("RVD.designer");
    public static final Logger controllerLogger = Logger.getLogger("RVD.controller");
}

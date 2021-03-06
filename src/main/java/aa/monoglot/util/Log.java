package aa.monoglot.util;

import aa.monoglot.Monoglot;
import aa.monoglot.misc.keys.AppString;
import aa.monoglot.misc.keys.LocalizationKey;

import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.logging.*;

public class Log {
    private static Logger logger = Logger.getLogger("aa.monoglot");

    static {
        logger.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new LogFormatter());
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
    }

    /**
     * Initializes logging to file in the directory at <code>path</code>
     */
    public static void init(Path path) throws IOException {
        try {
            path = path.resolve(Instant.now().toString().replaceAll(":","-") + ".log").toAbsolutePath();
            FileHandler fileHandler = new FileHandler(path.toString());
            fileHandler.setFormatter(new LogFormatter());
            fileHandler.setLevel(Level.ALL);

            logger.addHandler(fileHandler);
            logger.fine("Now logging to file: " + path.toString());
        } catch (IOException exception) {
            severe("Error occured in FileHandler.", exception);
            throw exception;
        }
    }

    public static void warning(String message) {
        logger.warning(message);
    }
    public static void warning(LocalizationKey key, Object... arguments) {
        if(Monoglot.getMonoglot() != null)
            logger.warning(Monoglot.getMonoglot().getLocalString(key, arguments));
    }

    public static void warning(String message, Throwable cause){
        logger.log(Level.WARNING, message, cause);
    }
    public static void warning(LocalizationKey key, Throwable cause, Object... arguments){
        if(Monoglot.getMonoglot() != null)
            logger.log(Level.WARNING, Monoglot.getMonoglot().getLocalString(key, arguments), cause);
    }

    public static void entering(String className, String methodName) {
        logger.entering(className, methodName);
    }

    public static void exiting(String className, String methodName) {
        logger.exiting(className, methodName);
    }

    public static void severe(String message, Throwable caught) {
        logger.log(Level.SEVERE, message, caught);
    }
    public static void severe(LocalizationKey key, Throwable caught, Object... arguments) {
        if(Monoglot.getMonoglot() != null)
            logger.log(Level.SEVERE, Monoglot.getMonoglot().getLocalString(key, arguments), caught);
    }

    public static void info(String message) {
        logger.info(message);
    }
    public static void info(LocalizationKey key, Object... arguments) {
        if(Monoglot.getMonoglot() != null)
            logger.info(Monoglot.getMonoglot().getLocalString(key, arguments));
    }

    public static void fine(String message) {
        logger.fine(message);
    }
    public static void fine(LocalizationKey key, Object... arguments) {
        if(Monoglot.getMonoglot() != null)
            logger.fine(Monoglot.getMonoglot().getLocalString(key, arguments));
    }

    public static void finer(String message) {
        logger.finer(message);
    }

    public static void message(AppString key, Object... arguments) {
        if(Monoglot.getMonoglot() != null)
            logger.info(MessageFormat.format(Monoglot.getMonoglot().getLocalString(key), arguments));
    }
}

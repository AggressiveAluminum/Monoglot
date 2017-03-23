package aa.monoglot.util;

import aa.monoglot.Monoglot;
import aa.monoglot.misc.keys.AppString;

import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.logging.*;

public class Log {
    private static Logger logger = Logger.getLogger("aa.monoglot");
    private static String logFileName;

    private static void createLogger() {
        logger = Logger.getLogger("aa.monoglot");
        logger.info("Logger name: " + logger.getName());
    }

    public static void init(Path filePath) throws IOException {
        //createLogger();
        createLogFile(filePath);
    }

    public static void warning(String message) {
        logger.warning(message);
    }

    public static void entering(String className, String methodName) {
        logger.entering(className, methodName);
    }

    public static void exiting(String className, String methodName) {
        logger.exiting(className, methodName);
    }

    public static void severe(String message, Object catched) {
        logger.log(Level.SEVERE, message, catched);
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void fine(String message) {
        logger.fine(message);
    }

    public static void message(AppString key, Object... arguments) {
        logger.info(MessageFormat.format(Monoglot.getLocalString(key), arguments));
    }

    public static String getFileName(){
        if(logFileName == null) {
            logFileName = Instant.now().toString().replaceAll(":","-") + ".log";
        }
        return logFileName;
    }

    private static void createLogFile(Path filePath) throws IOException {
        Handler fileHandler;
        Formatter simpleFormatter;

        try {
            fileHandler = new FileHandler(filePath.toAbsolutePath().toString());
            logger.addHandler(fileHandler);

            fileHandler.setFormatter(new LogFormatter());
            fileHandler.setLevel(Level.ALL);
            logger.setLevel(Level.ALL);

        } catch (IOException exception) {
            severe("Error occured in FileHandler.", exception);
            throw exception;
        }
    }
}

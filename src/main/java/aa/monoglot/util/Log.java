package aa.monoglot.util;

import javafx.scene.control.Alert;
import javafx.stage.Window;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.logging.*;

public class Log {
    private static Logger logger = null;
    private static String logFileName;

    private static void createLogger() {
        logger = Logger.getLogger("aa.monoglot");
        logger.info("Logger name: " + logger.getName());
    }

    public static void init(Path filePath) throws IOException {
        createLogger();
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
            simpleFormatter = new SimpleFormatter();
            logger.addHandler(fileHandler);

            info("Logger with default formatter.");
            fileHandler.setFormatter(simpleFormatter);
            fileHandler.setLevel(Level.ALL);
            logger.setLevel(Level.ALL);

            info("Logger with now with simple formatter.");

        } catch (IOException exception) {
            severe("Error occured in FileHandler.", exception);
            throw exception;
        }
    }
}

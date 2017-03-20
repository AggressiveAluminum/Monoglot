package aa.monoglot.Logs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.*;

/**
 * Created by Matt on 2/24/17.
 */
public class Log {

    private static Logger logger = null;

    private static void createLogger(){

        logger =  Logger.getLogger(aa.monoglot.Monoglot.class.getName());
        logger.info("Logger name: " + logger.getName());

    }

    public static void loggerInit(Path filePath){

        createLogger();
        createLogFile(filePath);

    }

    public static void logWarning(String message){

        logger.warning(message);

    }

    public static void logEnteringMethod(String className, String methodName){

        logger.entering(className, methodName);

    }

    public static void logExitingMethod(String className, String methodName){

        logger.exiting(className, methodName);

    }

    public static void logIssueSevere(String message, Object catched){

        logger.log(Level.SEVERE, message, catched);

    }

    public static void logInfo(String message){

        logger.info(message);

    }

    private static void createLogFile(Path filePath){
        logEnteringMethod("Log", "createLogFile");
        logWarning("File handler may throw and IOException.");

        Handler fileHandler = null;
        Formatter simpleFormatter = null;

        try{

            fileHandler = new FileHandler(filePath.toAbsolutePath().toString());
            simpleFormatter = new SimpleFormatter();
            logger.addHandler(fileHandler);

            logInfo("Logger with default formatter.");
            fileHandler.setFormatter(simpleFormatter);
            fileHandler.setLevel(Level.ALL);
            logger.setLevel(Level.ALL);

            logInfo("Logger with now with simple formatter.");

        }
        catch (IOException exception){
            logIssueSevere("Error occured in FileHandler.", exception);
        }

        logExitingMethod("Log", "createLogFile");

    }
}

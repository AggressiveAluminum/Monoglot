package aa.monoglot.Logs;

import java.util.logging.*;

/**
 * Created by Matt on 2/24/17.
 */
public class Log {

    private static Logger logger = null;



    public Log (String className){
        String loggerClassnameArgument = className.getClass().toString();

        logger = Logger.getLogger(loggerClassnameArgument);
    }

    public Logger getLogger(){

        return logger;
    }

    public void logIssueSevere(String message){
        LogRecord record = new LogRecord(Level.SEVERE, message);
        record.setLoggerName(logger.getName());
    }

    public void logIssueWarning(String message){
        LogRecord record = new LogRecord(Level.WARNING, message);
        record.setLoggerName(logger.getName());
    }

    public void logInfo(String message){
        LogRecord record = new LogRecord(Level.INFO, message);
        record.setLoggerName(logger.getName());
    }

    public void logIssueFine(String message){
        LogRecord record = new LogRecord(Level.FINE, message);
        record.setLoggerName(logger.getName());
    }

    public void logIssueFiner(String message){
        LogRecord record = new LogRecord(Level.FINER, message);
        record.setLoggerName(logger.getName());
    }

    public void logIssueFinest(String message){
        LogRecord record = new LogRecord(Level.FINEST, message);
        record.setLoggerName(logger.getName());
    }

    public void logIssueAll(String message){
        LogRecord record = new LogRecord(Level.ALL, message);
        record.setLoggerName(logger.getName());
    }

    public String printLog(){
        return null;
    }
}

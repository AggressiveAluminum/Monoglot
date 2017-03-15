package aa.monoglot.Logs;

import java.util.logging.*;

/**
 * Created by Matt on 2/24/17.
 */
public class Log {

    public static Logger logger = Logger.getLogger(aa.monoglot.Monoglot.class.getName());

    public Log(){
        logger.info("Logger name: " + logger.getName());
    }

    public static void logWarning(String message){

        logger.warning(message);

    }

    public static void logIssueSevere(String message){

    }

    public static void logIssueWarning(String message){


    }

    public static void logInfo(String message){


    }

    public static void logIssueFine(String message){


    }

    public static void logIssueFiner(String message){


    }

    public static void logIssueFinest(String message){


    }

    public static void logIssueAll(String message){


    }

    public void printLog(){


    }
}

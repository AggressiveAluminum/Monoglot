package aa.monoglot.Logs;

import java.util.logging.*;

/**
 * Created by Matt on 2/24/17.
 */
public class Log {

    private static Logger logger = null;



    public Log (String className){
        String loggerClassnameArguement = className + ".class.getName()";

        logger = Logger.getLogger(loggerClassnameArguement);
    }

    public Logger getLogger(){
        return logger;
    }
}

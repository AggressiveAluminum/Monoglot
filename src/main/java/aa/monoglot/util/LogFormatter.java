package aa.monoglot.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

class LogFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        StringWriter writer = new StringWriter();
        writer.write(String.format("%s - [%s][%s]", Instant.ofEpochMilli(record.getMillis()).toString(),
                record.getLevel().getLocalizedName(), record.getLoggerName()));
        boolean entry = false;
        if(record.getLevel() == Level.FINER){
            if(record.getMessage().equals("ENTRY"))
                writer.write(String.format(" Entering %s.%s\n", record.getSourceClassName(), record.getSourceMethodName()));
            else if(record.getMessage().equals("RETURN"))
                writer.write(String.format(" Exiting %s.%s\n", record.getSourceClassName(), record.getSourceMethodName()));
            else writer.write(String.format("[%s.%s] %s\n", record.getSourceClassName(), record.getSourceMethodName(), record.getMessage()));
        } else writer.write(String.format(" %s\n", record.getMessage()));

        if(record.getThrown() != null) {
            record.getThrown().printStackTrace(new PrintWriter(writer));
            writer.write('\n');
        }

        return writer.toString();
    }
}

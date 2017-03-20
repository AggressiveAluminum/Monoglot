package aa.monoglot;

import aa.monoglot.log.Log;
import aa.monoglot.util.OS;
import javafx.application.Application;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * @see Monoglot
 */
public class Main {
    public static void main(String[] args) throws IOException {
        OS.verify();
        Log.loggerInit(OS.SETTINGS_DIRECTORY.resolve("log-" + DateTimeFormatter.ISO_INSTANT.format(Instant.now()).replaceAll(":",".") + ".log"));
        Application.launch(Monoglot.class, args);
    }
}

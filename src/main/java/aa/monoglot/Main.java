package aa.monoglot;

import aa.monoglot.Logs.Log;
import aa.monoglot.util.OS;
import javafx.application.Application;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @see Monoglot
 */
public class Main {
    public static void main(String[] args) throws IOException {
        OS.verify();
        Log.loggerInit(OS.SETTINGS_DIRECTORY.resolve("log-" + DateTimeFormatter.ISO_INSTANT.format(LocalDateTime.now()) + ".log"));
        Application.launch(Monoglot.class, args);
    }
}

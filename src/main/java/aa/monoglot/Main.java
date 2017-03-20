package aa.monoglot;

import aa.monoglot.util.OS;
import javafx.application.Application;

import java.io.IOException;

/**
 * @see Monoglot
 */
public class Main {
    public static void main(String[] args) throws IOException {
        OS.verify();
        Application.launch(Monoglot.class, args);
    }
}

package aa.monoglot;

import com.sun.javafx.application.LauncherImpl;

/**
 * @see Monoglot
 */
public class Main {
    public static void main(String[] args){
        LauncherImpl.launchApplication(Monoglot.class, MonoglotPreloader.class, args);
    }
}

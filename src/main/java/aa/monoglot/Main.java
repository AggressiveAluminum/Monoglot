package aa.monoglot;

import aa.monoglot.ui.controller.SaveLoad;
import javafx.application.Application;

/**
 * @author cofl
 * @date 2/8/2017
 */
public class Main {
    public static void main(String[] args) throws java.io.IOException {
        //Application.launch(Monoglot.class, args);
        String path = "/Users/Darren/Desktop/";
        String name = "temp.mglt";
        String tempLoc = "/Users/Darren/Desktop/temp";
        SaveLoad.Save(path, name, tempLoc);
    }
}

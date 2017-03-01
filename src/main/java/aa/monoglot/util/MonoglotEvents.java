package aa.monoglot.util;

import aa.monoglot.Monoglot;

/**
 * Generic class for globabl application event dispatch.
 */
public class MonoglotEvents {
    public static void projectOpened(){
        //TODO: update the project in the controllers
        Monoglot.getMonoglot().mainController.setProjectControlsEnabled(true);
    }

    public static void projectClosed(){
        Monoglot.getMonoglot().mainController.setProjectControlsEnabled(false);
    }
}

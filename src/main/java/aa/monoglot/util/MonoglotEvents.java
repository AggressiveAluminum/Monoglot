package aa.monoglot.util;

import aa.monoglot.Monoglot;

import java.sql.SQLException;

/**
 * Generic class for globabl application event dispatch.
 */
public class MonoglotEvents {
    public static void projectOpened() throws SQLException {
        //TODO: update the project in the controllers
        Monoglot.getMonoglot().mainController.lexiconTabController.loadWordList();
        Monoglot.getMonoglot().mainController.setProjectControlsEnabled(true);
    }

    public static void projectClosed(){
        Monoglot.getMonoglot().mainController.setProjectControlsEnabled(false);
        Monoglot.getMonoglot().mainController.lexiconTabController.clearInfo();
    }
}

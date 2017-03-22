package aa.monoglot;

import aa.monoglot.ui.dialog.Dialogs;
import aa.monoglot.util.Log;
import javafx.application.Preloader;
import javafx.stage.Stage;

public class MonoglotPreloader extends Preloader {
    @Override
    public void start(Stage primaryStage) throws Exception {
        //TODO
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification notification){
        if(notification instanceof Preloader.ErrorNotification) {
            Log.severe(((ErrorNotification) notification).getDetails(), null);
            //Dialogs.error()
            //TODO
        }
        //TODO
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification notification){
        //TODO
    }
}

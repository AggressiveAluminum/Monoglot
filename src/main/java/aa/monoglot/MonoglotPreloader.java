package aa.monoglot;

import aa.monoglot.misc.keys.AppError;
import aa.monoglot.misc.keys.AppString;
import aa.monoglot.ui.dialog.Dialogs;
import aa.monoglot.util.Log;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.io.InputStream;

public class MonoglotPreloader extends Preloader {
    private Window window;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.window = primaryStage;

        Parent parent;
        try(InputStream stream = getClass().getClassLoader().getResourceAsStream("images/logo.png")){
            ImageView view = new ImageView(new Image(stream));
            parent = new AnchorPane(view);
        } catch (IOException e){
            //TODO: manually construct a placeholder UI
            parent = new AnchorPane(new Label("Monoglot"));
        }

        parent.setCache(true);
        parent.setCacheHint(CacheHint.SPEED);
        parent.setStyle("-fx-background-color: transparent;");
        Scene scene = new Scene(parent);
        scene.setFill(Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification notification){
        if(notification instanceof Preloader.ErrorNotification) {
            Log.severe(((ErrorNotification) notification).getDetails(), null);
            Dialogs.error(window, ((ErrorNotification) notification).getCause(),
                    Monoglot.getLocalString(AppError.LOAD_ERROR_TITLE, "Error during startup"),
                    Monoglot.getLocalString(AppError.LOAD_ERROR_HEADER, "An error occurred during startup."),
                    Monoglot.getLocalString(AppError.LOAD_ERROR_TEXT, "Unless you're doing something funky, please send this to the developer, along with an explanation of what you were doing:"))
                    .showAndWait();
            window.hide();
            Platform.exit();
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification notification){
        if(notification.getType() == StateChangeNotification.Type.BEFORE_START) {
            Log.fine(Monoglot.getLocalString(AppString.PRELOADER_DONE));
            window.hide();
        }
    }
}

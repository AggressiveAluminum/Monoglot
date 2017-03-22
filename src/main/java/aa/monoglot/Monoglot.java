package aa.monoglot;

import aa.monoglot.misc.ApplicationSettings;
import aa.monoglot.misc.keys.AppError;
import aa.monoglot.misc.keys.AppString;
import aa.monoglot.misc.keys.AppWarning;
import aa.monoglot.misc.keys.LocalizationKey;
import aa.monoglot.ui.controller.MonoglotController;
import aa.monoglot.ui.dialog.Dialogs;
import aa.monoglot.util.Log;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.DialogEvent;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * <h1>Monoglot -- A ConLang Dictionary Manager.</h1>
 * <br/>By Team Agressive Aluminum
 * <p><a href="http://github.com/AgressiveAluminum/Monoglot">http://github.com/AgressiveAluminum/Monoglot</a>
 *
 * @author amstanag
 * @author cofl
 * @author softwarexplorer
 * @author zefrof
 */
public class Monoglot extends Application {
    private static Monoglot instance;

    private ApplicationSettings applicationSettings;
    private ResourceBundle resourceBundle;

    private final List<Image> icons = new ArrayList<>();
    private AnchorPane uiRoot;
    private MonoglotController uiController;
    private Window window;

    @Override
    public void init(){
        instance = this;

        try { // Application settings, OS-specific tasks.
            applicationSettings = new ApplicationSettings();
        } catch(IOException e){
            notifyPreloader(new Preloader.ErrorNotification(null, "Failed to load application settings", e));
            instance = null;
            return;
        }

        try { // Logging.
            Log.init(applicationSettings.resolve(Log.getFileName()));
        } catch(IOException e){
            notifyPreloader(new Preloader.ErrorNotification(null, "Failed to initiate logging system", e));
            instance = null;
            return;
        }

        try { // Localization.
            resourceBundle = ResourceBundle.getBundle("local/lang");
        } catch(MissingResourceException e){
            notifyPreloader(new Preloader.ErrorNotification(null, "Failed to load application localization", e));
            instance = null;
            return;
        }

        // A global uncaught exception handler, for in case something escapes out the top of the application.
        // If something does get out, it's *really* wrong, and any attempts to fix it either failed or were nonexistent.
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            String msg = (resourceBundle != null && resourceBundle.containsKey(AppError.UNCAUGHT.getKey()))?getString(AppError.UNCAUGHT):
                    "An uncaught exception bubbled out of the application. We don't know what to do! Complain about it!";
            Log.severe(msg, throwable);
            Dialogs.error(window, throwable,
                    getLocalString(AppError.FATAL_ERROR_TITLE, "Fatal Error"),
                    getLocalString(AppError.FATAL_ERROR_HEADER, "An unrecoverable error occurred while loading the application."),
                    getLocalString(AppError.FATAL_ERROR_TEXT, "Unless you're doing something funky, please send this to the developer, along with an explanation of what you were doing:"))
                .showAndWait();
            Platform.exit();
        });

        try { // Get the UI now.
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/app.fxml"));
            uiRoot = loader.load();
            uiController = loader.getController();
        } catch (IOException e) {
            notifyPreloader(new Preloader.ErrorNotification(null, getString(AppError.LOAD_ERROR), e));
            instance = null;
            return;
        }

        // Load the shiny icons.
        try(InputStream image = getClass().getClassLoader().getResourceAsStream("images/logo.png");
            InputStream largeImage = getClass().getClassLoader().getResourceAsStream("images/logo-large.png")){
            Collections.addAll(icons, new Image(largeImage), new Image(image),
                    new Image(image, 128, 128, true, true),
                    new Image(image, 64, 64, true, true),
                    new Image(image, 32, 32, true, true),
                    new Image(image, 16, 16, true, true));
        } catch(IOException e){
            Log.warning(getString(AppWarning.ICON_LOAD));
            // Not that big a deal.
        }

        Platform.setImplicitExit(false);
        //TODO
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Log.entering(Monoglot.class.getName(), "start");
        if(instance == null)
            return;

        window = primaryStage;
        primaryStage.setScene(new Scene(uiRoot));
        primaryStage.setTitle(getString(AppString.APP_NAME));
        primaryStage.minHeightProperty().bind(uiRoot.minHeightProperty());
        primaryStage.maxHeightProperty().bind(uiRoot.maxHeightProperty());

        primaryStage.getIcons().setAll(icons);
        primaryStage.setOnCloseRequest(uiController::quitApplication);
        primaryStage.show();
    }

    @Override
    public void stop(){
        Log.entering(Monoglot.class.getName(), "start");
        if(instance == null)
            return;
        //TODO: if there are any end-tasks
    }

    public String getString(LocalizationKey key){
        return resourceBundle.getString(key.getKey());
    }
    public Window getWindow(){
        return window;
    }

    public static String getLocalString(LocalizationKey key){
        return instance.resourceBundle.getString(key.getKey());
    }
    public static String getLocalString(LocalizationKey key, String _default){
        if(instance != null && instance.resourceBundle != null && instance.resourceBundle.containsKey(key.getKey()))
            return instance.resourceBundle.getString(key.getKey());
        else return _default;
    }

    public static Monoglot getMonoglot() {
        return instance;
    }
}



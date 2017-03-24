package aa.monoglot;

import aa.monoglot.misc.ApplicationSettings;
import aa.monoglot.misc.keys.AppError;
import aa.monoglot.misc.keys.AppString;
import aa.monoglot.misc.keys.AppWarning;
import aa.monoglot.misc.keys.LocalizationKey;
import aa.monoglot.ui.controller.MonoglotController;
import aa.monoglot.ui.dialog.Dialogs;
import aa.monoglot.util.Log;
import aa.monoglot.util.UT;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
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

    public static Monoglot getMonoglot() {
        return instance;
    }

    private ApplicationSettings applicationSettings;
    private ResourceBundle resourceBundle;

    private final List<Image> icons = new ArrayList<>();
    private Scene uiScene;
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
            Path p = applicationSettings.resolve("logs");
            if(Files.isRegularFile(p))
                Files.delete(p);
            if(!Files.exists(p))
                Files.createDirectories(p);
            Log.init(p.resolve(Log.getFileName()));
        } catch(IOException e){
            notifyPreloader(new Preloader.ErrorNotification(null, "Failed to initiate logging system", e));
            instance = null;
            return;
        }

        try { // Localization.
            resourceBundle = ResourceBundle.getBundle("local/lang");
        } catch(MissingResourceException e){
            Log.severe("An unrecoverable error occurred while loading the application.", e);
            notifyPreloader(new Preloader.ErrorNotification(null, "Failed to load application localization", e));
            instance = null;
            return;
        }
        Log.message(AppString.LOADED_RESOURCE_BUNDLE, resourceBundle.getLocale().toLanguageTag());

        // A global uncaught exception handler, for in case something escapes out the top of the application.
        // If something does get out, it's *really* wrong, and any attempts to fix it either failed or were nonexistent.
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            String msg = (resourceBundle != null && resourceBundle.containsKey(AppError.UNCAUGHT.getKey()))?getLocalString(AppError.UNCAUGHT):
                    "An uncaught exception bubbled out of the application. We don't know what to do! Complain about it!";
            Log.severe(msg, throwable);
            Dialogs.error(window, throwable,
                    UT.c(getLocalString(AppError.FATAL_ERROR_TITLE), "Fatal Error"),
                    UT.c(getLocalString(AppError.FATAL_ERROR_HEADER), "An unrecoverable error occurred while loading the application."),
                    UT.c(getLocalString(AppError.FATAL_ERROR_TEXT), "Unless you're doing something funky, please send this to the developer, along with an explanation of what you were doing:"))
                .showAndWait();
            Platform.exit();
        });

        try { // Get the UI now.
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/app.fxml"), resourceBundle);
            uiScene = new Scene(loader.load());
            uiController = loader.getController();
        } catch (IOException e) {
            Log.severe(getLocalString(AppError.LOAD_ERROR), e);
            notifyPreloader(new Preloader.ErrorNotification(null, getLocalString(AppError.LOAD_ERROR), e));
            instance = null;
            return;
        }
        Log.message(AppString.LOADED_UI_FILES);

        // Load the shiny icons.
        try(InputStream image = getClass().getClassLoader().getResourceAsStream("images/logo.png");
            InputStream largeImage = getClass().getClassLoader().getResourceAsStream("images/logo-large.png")){
            Collections.addAll(icons, new Image(largeImage), new Image(image),
                    new Image(image, 128, 128, true, true),
                    new Image(image, 64, 64, true, true),
                    new Image(image, 32, 32, true, true),
                    new Image(image, 16, 16, true, true));
            Log.message(AppString.LOADED_ICONS);
        } catch(IOException e){
            Log.warning(getLocalString(AppWarning.ICON_LOAD));
            // Not that big a deal.
        }

        Log.exiting(Monoglot.class.getName(), "init");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Log.entering(Monoglot.class.getName(), "start");
        if(instance == null)
            return;
        window = primaryStage;

        primaryStage.setScene(uiScene);
        primaryStage.setTitle(getLocalString(AppString.APP_NAME));
        primaryStage.getIcons().setAll(icons);
        primaryStage.minHeightProperty().bind(((BorderPane) uiScene.getRoot()).minHeightProperty());
        primaryStage.minWidthProperty().bind(((BorderPane) uiScene.getRoot()).minWidthProperty());

        primaryStage.setOnCloseRequest(uiController::wQuitApplication);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
        primaryStage.setAlwaysOnTop(false);
        Log.exiting(Monoglot.class.getName(), "start");
    }

    @Override
    public void stop(){
        Log.entering(Monoglot.class.getName(), "stop");
        if(instance == null)
            return;
        //TODO: if there are any end-tasks
        Log.exiting(Monoglot.class.getName(), "stop");
    }

    public Window getWindow(){
        return window;
    }

    public String getLocalString(LocalizationKey key){
        return resourceBundle.getString(key.getKey());
    }
    public String getLocalString(LocalizationKey key, Object... arguments){
        return MessageFormat.format(resourceBundle.getString(key.getKey()), arguments);
    }

    public List<Image> getIcons() {
        return icons;
    }
}



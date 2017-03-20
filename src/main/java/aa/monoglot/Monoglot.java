package aa.monoglot;

import aa.monoglot.ui.controller.MonoglotController;
import aa.monoglot.util.ApplicationErrorCode;
import aa.monoglot.util.OS;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * <h1>Monoglot -- A ConLang Dictionary Manager.</h1>
 * <br/>By Team Agressive Aluminum
 * <p><a href="http://github.com/AgressiveAluminum/Monoglot">http://github.com/AgressiveAluminum/Monoglot</a>
 *
 * @author amstanag
 * @author cofl
 * @author softwarexplorer
 * @author MaxSimo
 * @author zefrof
 */
public class Monoglot extends Application {
    private static Monoglot monoglot;

    public Stage window;
    public ResourceBundle bundle;
    public MonoglotController mainController;
    public final List<Image> icons = new ArrayList<>();

    public void start(Stage primaryStage){
        window = primaryStage;
        monoglot = this;

        Thread.setDefaultUncaughtExceptionHandler(this::uncaughtExceptionHandler);

        try {
            bundle = ResourceBundle.getBundle("lang/lang");
        } catch(Exception e){
            // Definitely can't recover from a bundle load error. No strings == no usability. Also crashes.
            showError(e, ApplicationErrorCode.LOCALIZATION_FAILURE);
            Platform.exit();
        }

        try {   // Load the main interface.
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/app.fxml"), bundle);
            AnchorPane p = loader.load();
            mainController = loader.getController();

            window.setScene(new Scene(p));
            window.setTitle(bundle.getString("app.title"));

            // Bind the min dimensions, because for some reason this isn't done already
            // and using <Stage> as the root is dirty, I think. -- cofl
            window.minHeightProperty().bind(p.minHeightProperty());
            window.minWidthProperty().bind(p.minWidthProperty());
        } catch(Exception e){
            // Can't really recover from this :(
            showError(e, ApplicationErrorCode.APPLICATION_LOAD_FAILURE);
            Platform.exit();
        }

        try(InputStream image = getClass().getClassLoader().getResourceAsStream("images/logo.png");
            InputStream largeIcon = getClass().getClassLoader().getResourceAsStream("images/logo-large.png")){
            // do we need largeIcon? -- cofl
            // Set the application icons.
            Collections.addAll(icons, new Image(largeIcon),
                new Image(image),
                new Image(image, 128, 128, true, true),
                new Image(image, 64, 64, true, true),
                new Image(image, 32, 32, true, true),
                new Image(image, 16, 16, true, true)
            );
            primaryStage.getIcons().addAll(icons);
        } catch(Exception e){
            // fail silently, this part isn't vital.
        }

        /* TODO: open last project?
           It'd be nice, I think, but we'd have to store a config file somewhere. Not this version.
        */

        Platform.setImplicitExit(false);
        window.setOnCloseRequest(mainController::quitApplication);
        window.show();
    }

    /**
     * A global uncaught exception handler, for in case something escapes out the top of the application.
     * If something does get out, it's *really* wrong, and any attempts to fix it either failed or were
     * nonexistent.
     *
     * <p>See line #42 (last updated 6:50pm 22 Feb 2017)
     */
    private void uncaughtExceptionHandler(Thread thread, Throwable throwable) {
        showError(throwable, ApplicationErrorCode.UNUSUAL_FAILURE);
        Platform.exit();
    }

    /**
     * Show an error to the user. If the code is denoted "Fatal", exit the application abnormally.
     * @param throwable The error to be shown.
     * @param code What kind of error this is.
     */
    public void showError(Throwable throwable, ApplicationErrorCode code){
        Alert error = new Alert(Alert.AlertType.ERROR);

        String title, header, text;
        if(code == ApplicationErrorCode.LOCALIZATION_FAILURE){
            title = "Resource Load Error";
            header = "An error occurred while loading the application localization file.";
            text = "Unless you're doing something funky, please send this to the developer," +
                    " along with an explanation of what you were doing:";
        } else {
            title = bundle.getString(code.getPrefix() + ".title");
            header = bundle.getString(code.getPrefix() + ".header");
            text = bundle.getString(code.getPrefix() + ".text");

            if(code != ApplicationErrorCode.APPLICATION_LOAD_FAILURE && window != null && window.isShowing())
                error.initOwner(window);
        }

        error.setTitle(title);
        error.setHeaderText(header);
        error.setContentText(text);

        if(code.isFatal()) { // if we can't recover, why?
            StringWriter stringWriter = new StringWriter();
            throwable.printStackTrace(new PrintWriter(stringWriter));
            TextArea area = new TextArea(stringWriter.toString());
            area.setEditable(false);
            area.setWrapText(true);
            error.getDialogPane().setExpandableContent(area);
        }

        error.showAndWait();

        if(code.isFatal())
            Platform.exit();
    }

    /**
     * Get the application instance.
     */
    public static Monoglot getMonoglot() {
        return monoglot;
    }
}

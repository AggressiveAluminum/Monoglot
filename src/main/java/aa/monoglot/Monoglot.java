package aa.monoglot;

import aa.monoglot.ui.controller.MonoglotController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Path;
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
    public Stage window;
    public ResourceBundle bundle;
    public MonoglotController mainController;
    public List<Image> icons = new ArrayList<>();

    private Project project;

    private static Monoglot monoglot;

    /**
     * {@inheritDoc}
     */
    public void start(Stage primaryStage){
        window = primaryStage;
        monoglot = this;

        Thread.setDefaultUncaughtExceptionHandler(this::uncaughtExceptionHandler);

        try {
            bundle = ResourceBundle.getBundle("lang/lang");
        } catch(Exception e){
            // Definitely can't recover from a bundle load error. No strings == no usability. Also crashes.
            showError(e, null);
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
            showError(e, true);
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
        showError(throwable, true);
        Platform.exit();
    }

    /**
     * Show a non-fatal error with localized keys.
     */
    public void showError(Exception e){
        showError(e, false);
    }

    /**
     * Shows an error.
     * @param e The error.
     * @param isFatal If <code>null</code>, use string literals (really bad). Otherwise, whether or not the exception is recoverable.
     */
    public void showError(Throwable e, Boolean isFatal) {
        Alert error = new Alert(Alert.AlertType.ERROR);

        String title, header, text;
        if(isFatal == null) {
            title = "Resource Load Error";
            header = "An error occurred while loading the application localization file.";
            text = "Unless you're doing something funky, please send this to the developer," +
                    " along with an explanation of what you were doing:";
        } else if(isFatal){
            title = bundle.getString("dialog.error.fatalError.title");
            header = bundle.getString("dialog.error.fatalError.header");
            text = bundle.getString("dialog.error.fatalError.text");
        } else {
            title = bundle.getString("dialog.error.normal.title");
            header = bundle.getString("dialog.error.normal.header");
            text = bundle.getString("dialog.error.normal.text");
        }

        /*if(isFatal != null) // for some reason this doesn't work, I can't pin down when or why.
            error.initOwner(window);*/
        error.setTitle(title);
        error.setHeaderText(header);
        error.setContentText(text);

        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        TextArea area = new TextArea(stringWriter.toString());
        area.setEditable(false);
        area.setWrapText(true);

        error.getDialogPane().setExpandableContent(area);
        error.showAndWait();
    }

    /**
     * Get the application instance.
     */
    public static Monoglot getMonoglot() {
        return monoglot;
    }

    /**
     * Get the current project.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Creates a new, empty project with no save file to back it up.
     * @throws IOException
     */
    public void newProject() throws IOException {
        project = new Project();
    }

    /**
     * Opens a project from a file.
     * @param path The project file.
     * @throws IOException
     */
    public void openProject(Path path) throws IOException {
        project = new Project(path);
    }

    /**
     * Cleans up and disposes the current project.
     * <br/><b>Does not check if saving needs to be done!</b>
     */
    public void closeProject() {
        if(project != null) {
            project.close();
            project = null;
        }
    }

    /**
     * Opens a project from a directory, with no attached save file.
     * This is useful if the application crashes horribly before you can save, so you can
     * still pick up the pieces.
     * @param path The working directory where the undead project resides.
     * @throws IOException
     */
    public void recoverProject(Path path) throws IOException {
        project = new Project(path, true);
    }
}

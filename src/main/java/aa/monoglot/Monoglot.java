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

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ResourceBundle;

/**
 * @author cofl
 * @date 2/8/2017
 */
public class Monoglot extends Application {
    public Stage window;
    public ResourceBundle bundle;
    public MonoglotController mainController;

    public Project project = new Project();

    private static Monoglot monoglot;

    public void start(Stage primaryStage){
        window = primaryStage;
        monoglot = this;

        try {
            bundle = ResourceBundle.getBundle("lang/lang");
        } catch(Exception e){
            showError(e, null);
            Platform.exit();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/app.fxml"), bundle);
            AnchorPane p = loader.load();
            mainController = loader.getController();

            window.setScene(new Scene(p));
            window.setTitle(bundle.getString("app.title"));

            window.minHeightProperty().bind(p.minHeightProperty());
            window.minWidthProperty().bind(p.minWidthProperty());
        } catch(Exception e){
            showError(e, true);
            Platform.exit();
        }

        try {
            InputStream image = getClass().getClassLoader().getResourceAsStream("images/logo.png");
            primaryStage.getIcons().addAll(
                    new Image(image),
                    new Image(image, 128, 128, true, true),
                    new Image(image, 64, 64, true, true),
                    new Image(image, 32, 32, true, true),
                    new Image(image, 16, 16, true, true)
            );
        } catch(Exception e){
            // fail silently, this part isn't vital.
        }

        try {
            //TODO: get temp working directory
            //project.setWorkingDirectory();
        } catch (Exception e){

        }

        window.show();
    }

    public void showError(Exception e){
        showError(e, false);
    }

    public void showError(Exception e, Boolean isFatal) {
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

    public static Monoglot getMonoglot() {
        return monoglot;
    }
}

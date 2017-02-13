package aa.monoglot;

import aa.monoglot.ui.controller.MonoglotController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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
    private static Monoglot monoglot;

    public void start(Stage primaryStage){
        window = primaryStage;
        monoglot = this;

        try {
            bundle = ResourceBundle.getBundle("lang/lang");
        } catch(Exception e){
            Alert fatalError = new Alert(Alert.AlertType.ERROR);
            fatalError.setTitle("Resource Load Error");
            fatalError.setHeaderText("An error occurred while loading the application localization file.");
            fatalError.setContentText("Unless you're doing something funky, please send this to the developer," +
                    " along with an explanation of what you were doing:");

            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            TextArea area = new TextArea(stringWriter.toString());
            area.setEditable(false);
            area.setWrapText(true);

            fatalError.getDialogPane().setExpandableContent(area);
            fatalError.showAndWait();
            Platform.exit();
        }

        try {
             // comment this line to enable error test
            /*if(Math.random() < Double.POSITIVE_INFINITY)
                throw new Exception();
            // */
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/app.fxml"), bundle);
            AnchorPane p = loader.load();
            mainController = loader.getController();

            window.setScene(new Scene(p));
            window.setTitle(bundle.getString("app.title"));

            window.minHeightProperty().bind(p.minHeightProperty());
            window.minWidthProperty().bind(p.minWidthProperty());
        } catch(Exception e){
            Alert fatalError = new Alert(Alert.AlertType.ERROR);
            fatalError.setTitle(bundle.getString("dialog.error.fatalError.title"));
            fatalError.setHeaderText(bundle.getString("dialog.error.fatalError.header"));
            fatalError.setContentText(bundle.getString("dialog.error.fatalError.text"));

            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            TextArea area = new TextArea(stringWriter.toString());
            area.setEditable(false);
            area.setWrapText(true);

            fatalError.getDialogPane().setExpandableContent(area);
            fatalError.showAndWait();
            Platform.exit();
        }

        window.show();
    }

    public void showError(Exception e) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle(bundle.getString("dialog.error.normal.title"));
        error.setHeaderText(bundle.getString("dialog.error.normal.header"));
        error.setContentText(bundle.getString("dialog.error.fatalError.text"));

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

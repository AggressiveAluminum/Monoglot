package aa.monoglot;

import aa.monoglot.ui.controller.MonoglotController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * @author cofl
 * @date 2/8/2017
 */
public class Monoglot extends Application {
    public Stage window;
    public ResourceBundle bundle;
    public MonoglotController mainController;

    public void start(Stage primaryStage){
        window = primaryStage;
        bundle = ResourceBundle.getBundle("lang/lang");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/app.fxml"), bundle);
            AnchorPane p = loader.load();
            mainController = loader.getController();

            window.setScene(new Scene(p));
            window.setTitle(bundle.getString("app.title"));

            window.minHeightProperty().bind(p.minHeightProperty());
            window.minWidthProperty().bind(p.minWidthProperty());
        } catch(IOException e){
            //TODO: better error handling
            e.printStackTrace();
            Platform.exit();
        }

        window.show();
    }
}

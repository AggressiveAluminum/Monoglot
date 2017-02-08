package aa.monoglot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * @author cofl
 * @date 2/8/2017
 */
public class Monoglot extends Application {
    public Stage window;

    public void start(Stage primaryStage){
        window = primaryStage;

        try {
            ResourceBundle bundle = ResourceBundle.getBundle("lang/lang");
            BorderPane p = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/app.fxml"), bundle);

            window.setScene(new Scene(p));
            window.setTitle(bundle.getString("app.title"));
        } catch(IOException e){
            return;
        }

        window.show();
    }
}

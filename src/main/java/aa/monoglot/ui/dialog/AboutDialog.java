package aa.monoglot.ui.dialog;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ResourceBundle;

/**
 * @author cofl
 * @date 2/13/2017
 */
public class AboutDialog extends Stage {
    public AboutDialog(ResourceBundle bundle) throws Exception {
        //TODO: make this prettier and more functional!
        Parent p = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/dialog/about.fxml"), bundle);
        setScene(new Scene(p));
        setTitle(bundle.getString("dialog.about.title"));
        setResizable(false);
    }
}

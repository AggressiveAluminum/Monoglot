package aa.monoglot.ui.dialog;

import aa.monoglot.Monoglot;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;

import java.util.ResourceBundle;

/**
 * @author cofl
 * @date 2/13/2017
 */
public class AboutDialog extends DismissableDialog {
    public AboutDialog(ResourceBundle bundle) throws Exception {
        //TODO: make this prettier and more functional!
        Parent p = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/dialog/about.fxml"), bundle);
        getDialogPane().contentProperty().set(p);
        setTitle(bundle.getString("dialog.about.title"));
        setResizable(false);

        if(!Monoglot.getMonoglot().icons.isEmpty()){
            ImageView i = new ImageView(Monoglot.getMonoglot().icons.get(0));
            i.setFitWidth(256);
            i.setFitHeight(256);
            getDialogPane().setGraphic(i);
        }
    }
}

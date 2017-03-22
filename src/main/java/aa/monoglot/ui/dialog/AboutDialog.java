package aa.monoglot.ui.dialog;

import old.monoglot.Monoglot;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutDialog extends DismissableDialog {
    public AboutDialog(ResourceBundle bundle) throws Exception {
        //TODO: make this prettier and more functional!
        URL url = getClass().getClassLoader().getResource("fxml/dialog/about.fxml");
        if(url == null) //TODO: make this exception more specific.
            throw new Exception("fxml/dialog/about.fxml");
        Parent p = FXMLLoader.load(url, bundle);
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

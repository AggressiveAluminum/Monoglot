package aa.monoglot.ui.controller;

import aa.monoglot.ui.ControlledTab;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import aa.monoglot.Monoglot;

/**
 * No-op controller. Does nothing other than register itself. Has some convenience methods
 * for handling basic things.
 */
public class GenericController implements GeneralController {
    @FXML private ControlledTab tab;
    @FXML private void initialize(){
        if(tab != null)
            tab.controller(this);
    }

    @FXML
    private void openLink(ActionEvent e){
        if((e.getSource() instanceof Hyperlink)) {
            Hyperlink link = (Hyperlink) e.getSource();
            Monoglot.getMonoglot().getHostServices().showDocument(link.getText());
        }
    }
}

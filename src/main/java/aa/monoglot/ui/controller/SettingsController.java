package aa.monoglot.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * @author cofl
 * @date 2/13/2017
 */
public class SettingsController extends AbstractChildController<MonoglotController> {
    @FXML private TextField projectName;
    @FXML private TextArea projectNotes;

    protected void postInitialize() {
        //nop
    }

    public void cancelChanges(ActionEvent event) {
    }

    public void saveChanges(ActionEvent event) {
    }

    protected  void clearInfo(){
        //TODO
    }

    public void projectChanged() {
        //TODO
    }
}

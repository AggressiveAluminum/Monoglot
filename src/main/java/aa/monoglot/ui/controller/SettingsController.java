package aa.monoglot.ui.controller;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * @author cofl
 * @date 2/13/2017
 */
public class SettingsController extends AbstractChildController<MonoglotController> {
    @FXML private JFXTextField projectName;
    @FXML private JFXTextArea projectNotes;

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
}

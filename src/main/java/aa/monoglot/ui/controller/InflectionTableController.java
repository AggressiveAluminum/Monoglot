package aa.monoglot.ui.controller;

import aa.monoglot.ui.ControlledTab;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class InflectionTableController implements GeneralController {
    @FXML
    private ControlledTab tab;
    @FXML
    private ComboBox<?> InflectionTable;

    @FXML
    private void initialize() {

        tab.controller(this);
    }

    public void tableSelect(ActionEvent actionEvent) {
        //getting the string that was in the ComboBox that was clicked
        String itemName = (String) (InflectionTable.getSelectionModel().getSelectedItem());

        if (itemName.equals("Types")) {

        } else {
            
        }
    }
}

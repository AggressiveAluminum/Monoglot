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
    private void initialize(){
        tab.controller(this);
    }

    public void tableSelect(ActionEvent actionEvent) {
        String itemName = (String)(InflectionTable.getSelectionModel().getSelectedItem());

        if(itemName.equals("Language 1")){
        System.out.println("Hello");
        }else{
            System.out.println("NOPE");
        }

    }
}

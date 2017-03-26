package aa.monoglot.ui.controller;

import aa.monoglot.ui.ControlledTab;
import javafx.fxml.FXML;

public class InflectionTableController implements GeneralController {
    @FXML
    private ControlledTab tab;

    @FXML
    private void initialize(){
        tab.controller(this);
    }
}

package aa.monoglot.ui;

import aa.monoglot.ui.controller.GeneralController;
import javafx.scene.control.Tab;

public class ControlledTab extends Tab {
    private GeneralController controller;
    public void setController(GeneralController controller){
        this.controller = controller;
    }

    public GeneralController getController() {
        return controller;
    }
    //TODO
}

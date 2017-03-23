package aa.monoglot.ui;

import aa.monoglot.ui.controller.GeneralController;
import javafx.scene.control.Tab;

public class ControlledTab extends Tab {
    private GeneralController tabController;
    public void controller(GeneralController controller){
        this.tabController = controller;
    }

    public GeneralController controller() {
        return tabController;
    }
    //TODO
}

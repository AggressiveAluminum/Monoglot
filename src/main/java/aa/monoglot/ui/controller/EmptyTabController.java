package aa.monoglot.ui.controller;

import aa.monoglot.project.Project;
import aa.monoglot.ui.ControlledTab;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;

public class EmptyTabController implements GeneralController {
    @FXML private ControlledTab tab;
    private MonoglotController master;
    @FXML private void initialize(){
        tab.controller(this);
    }

    public void setMaster(MonoglotController controller){
        master = controller;
    }

    @FXML
    private void createProject(ActionEvent event) throws SQLException, IOException, ClassNotFoundException {
        if(master != null)
            master.newProject(event);
    }

    @FXML
    private void openProject(ActionEvent event) throws SQLException, IOException, ClassNotFoundException {
        if(master != null)
            master.openProject(event);
    }
}

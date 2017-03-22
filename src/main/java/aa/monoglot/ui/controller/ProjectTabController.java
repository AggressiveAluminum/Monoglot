package aa.monoglot.ui.controller;

import aa.monoglot.project.Project;
import aa.monoglot.project.ProjectKey;
import aa.monoglot.ui.ControlledTab;
import aa.monoglot.util.BackedSettings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

import static aa.monoglot.project.ProjectKey.K_NAME;
import static aa.monoglot.project.ProjectKey.K_NOTES;

public class ProjectTabController implements GeneralController {
    @FXML private ControlledTab tab;
    @FXML private TextField projectName;
    @FXML private TextArea projectNotes;
    @FXML private void initialize(){
        tab.setController(this);
    }

    @FXML private void saveHandler(ActionEvent event){
        save();
    }

    @FXML private void cancelHandler(ActionEvent event){
        onLoad();
    }

    @Override
    public boolean save(){
        try {
            BackedSettings<ProjectKey> settings = Project.getProject().getSettings();
            if(!settings.get(K_NAME, "").equals(projectName.getText())){
                settings.put(K_NAME, projectName.getText());
                Project.getProject().markSaveNeeded();
            }
            if(!settings.get(K_NOTES, "").equals(projectNotes.getText())){
                settings.put(K_NOTES, projectNotes.getText());
                Project.getProject().markSaveNeeded();
            }
            if(Project.getProject().saveNeeded())
                settings.store(null);
            return true;
        } catch (IOException e) {
            //TODO: tell someone
        }
        return false;
    }

    @Override
    public boolean onLoad(){
        projectName.setText(Project.getProject().getSettings().get(K_NAME, ""));
        projectNotes.setText(Project.getProject().getSettings().get(K_NOTES, ""));
        return true;
    }
}

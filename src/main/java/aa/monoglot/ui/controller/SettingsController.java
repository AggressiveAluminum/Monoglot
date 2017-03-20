package aa.monoglot.ui.controller;

import aa.monoglot.project.Project;
import aa.monoglot.util.SilentException;
import aa.monoglot.util.UT;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class SettingsController extends AbstractChildController<MonoglotController> {
    @FXML private TextField projectName;
    @FXML private TextArea projectNotes;

    protected void postInitialize() {
        //nop
    }

    public void cancelChanges(ActionEvent event) {
        projectName.setText(Project.getProject().settings.getProperty("name", ""));
        projectNotes.setText(Project.getProject().settings.getProperty("notes",""));
    }

    public void saveChanges(ActionEvent event) throws IOException {
        Project.getProject().settings.setProperty("name", UT.c(projectName.getText()));
        Project.getProject().settings.setProperty("notes", UT.c(projectNotes.getText()));
        try(OutputStream stream = Files.newOutputStream(Project.getProject().settingsFile)){
            Project.getProject().settings.store(stream, "");
            stream.flush();
        }
    }

    protected  void clearInfo(){
        projectName.setText("");
        projectNotes.setText("");
    }

    @Override
    public boolean load(){
        projectName.setText(Project.getProject().settings.getProperty("name",""));
        projectNotes.setText(Project.getProject().settings.getProperty("notes",""));
        return true;
    }

    @Override
    public  boolean unload(){
        try {
            saveChanges(null);
        } catch (IOException e){
            SilentException.rethrow(e);
        }
        return true;
    }
}

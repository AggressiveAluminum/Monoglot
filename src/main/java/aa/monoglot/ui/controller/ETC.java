package aa.monoglot.ui.controller;

import aa.monoglot.util.ApplicationErrorCode;
import aa.monoglot.Monoglot;
import aa.monoglot.project.Project;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

class ETC {
    static void recoverWorkingDirectory(MonoglotController controller){
        DirectoryChooser chooser = new DirectoryChooser();
        File file = chooser.showDialog(Monoglot.getMonoglot().window);
        if(file != null){
            try {
                Project.openProject(file.toPath());
                controller.setProjectControlsEnabled(true);
            } catch(ClassNotFoundException | SQLException e){
                Monoglot.getMonoglot().showError(e, ApplicationErrorCode.RECOVERABLE_ERROR);
                //TODO: tell the user.
            } catch(IOException e){
                //TODO: tell the user.
            }
        }
    }

    static void openProject(MonoglotController controller){
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(controller.mgltExtensionFilter);
        chooser.setSelectedExtensionFilter(controller.mgltExtensionFilter.get(0));
        File file = chooser.showOpenDialog(Monoglot.getMonoglot().window);

        if(file != null){
            try {
                Project.openProject(file.toPath());
                controller.setProjectControlsEnabled(true);
            } catch(IOException | ClassNotFoundException | SQLException e){
                //TODO: tell the user.
            }
        }
    }

    static void newProject(MonoglotController controller){
        try {
            Project.openProject();
            controller.setProjectControlsEnabled(true);
        } catch(IOException | ClassNotFoundException | SQLException e) {
            //TODO: tell the user.
        }
    }

    static void saveProject(MonoglotController controller){
        if(!Project.isProjectOpen())
            return;
        if(!Project.getProject().hasSavePath()){
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().addAll(controller.mgltExtensionFilter);
            chooser.setSelectedExtensionFilter(controller.mgltExtensionFilter.get(0));
            File f = chooser.showSaveDialog(Monoglot.getMonoglot().window);
            if(f != null) {
                Path file = f.toPath();
                if (!Files.isDirectory(file))
                    Project.getProject().setSaveFile(file);
            }
        }
        if(Project.getProject().hasSavePath())
            controller.setLocalStatus(Project.getProject().save()?"app.status.saved":"app.status.saveFailed");
    }

    static void saveProjectAs(MonoglotController controller){
        if(!Project.isProjectOpen())
            return;
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(controller.mgltExtensionFilter);
        chooser.setSelectedExtensionFilter(controller.mgltExtensionFilter.get(0));
        File f = chooser.showSaveDialog(Monoglot.getMonoglot().window);
        if(f != null) {
            Path file = f.toPath();
            if (!Files.isDirectory(file))
                Project.getProject().setSaveFile(file);
            controller.setLocalStatus(Project.getProject().save()?"app.status.saved":"app.status.saveFailed");
        }
    }
}

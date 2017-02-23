package aa.monoglot.ui.controller;

import aa.monoglot.Monoglot;
import aa.monoglot.Project;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author cofl
 * @date 2/22/2017
 */
class ETC {
    static void recoverWorkingDirectory(MonoglotController controller){
        DirectoryChooser chooser = new DirectoryChooser();
        File file = chooser.showDialog(Monoglot.getMonoglot().window);
        if(file != null){
            try {
                Monoglot.getMonoglot().recoverProject(file.toPath());
                controller.setProjectControlsEnabled(true);
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
                Monoglot.getMonoglot().openProject(file.toPath());
                controller.setProjectControlsEnabled(true);
            } catch(IOException e){
                //TODO: tell the user.
            }
        }
    }

    static void newProject(MonoglotController controller){
        try {
            Monoglot.getMonoglot().newProject();
            controller.setProjectControlsEnabled(true);
        } catch (IOException e){
            //TODO: tell the user.
        }
    }

    static void saveProject(MonoglotController controller){
        Project project = Monoglot.getMonoglot().getProject();
        if(project == null)
            return;
        if(!project.hasSavePath()){
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().addAll(controller.mgltExtensionFilter);
            chooser.setSelectedExtensionFilter(controller.mgltExtensionFilter.get(0));
            File f = chooser.showSaveDialog(Monoglot.getMonoglot().window);
            if(f != null) {
                Path file = f.toPath();
                if (!Files.isDirectory(file))
                    project.setSaveFile(file);
            }
        }
        if(project.hasSavePath()) {
            controller.setLocalStatus(project.save()?"app.status.saved":"app.status.saveFailed");
        }
    }

    static void saveProjectAs(MonoglotController controller){
        Project project = Monoglot.getMonoglot().getProject();
        if(project == null)
            return;
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(controller.mgltExtensionFilter);
        chooser.setSelectedExtensionFilter(controller.mgltExtensionFilter.get(0));
        File f = chooser.showSaveDialog(Monoglot.getMonoglot().window);
        if(f != null) {
            Path file = f.toPath();
            if (!Files.isDirectory(file))
                project.setSaveFile(file);
            controller.setLocalStatus(project.save()?"app.status.saved":"app.status.saveFailed");
        }
    }
}

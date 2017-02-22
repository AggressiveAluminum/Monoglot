package aa.monoglot.io;

import aa.monoglot.Monoglot;
import aa.monoglot.Project;
import aa.monoglot.ui.controller.MonoglotController;
import aa.monoglot.ui.dialog.ConfirmOverwriteAlert;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cofl
 * @date 2/22/2017
 */
public class IO {
    private static final Map<String, String> ZIP_FILE_SETTINGS;

    static {
        ZIP_FILE_SETTINGS = new HashMap<>();
        ZIP_FILE_SETTINGS.put("create", "true");
    }

    public static void saveProject(MonoglotController controller){
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
                if (Files.exists(file) && !Files.isDirectory(file) && new ConfirmOverwriteAlert(Monoglot.getMonoglot().window,
                        controller.resources.getString("dialog.confirmOverwrite.text")).showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    project.setSaveFile(file);
                } else project.setSaveFile(file);
            }
        }
        if(project.hasSavePath()) {
            controller.setLocalStatus(project.save()?"app.status.saved":"app.status.saveFailed");
        }
    }

    public static void saveProjectAs(MonoglotController controller){
        Project project = Monoglot.getMonoglot().getProject();
        if(project == null)
            return;
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(controller.mgltExtensionFilter);
        chooser.setSelectedExtensionFilter(controller.mgltExtensionFilter.get(0));
        File f = chooser.showSaveDialog(Monoglot.getMonoglot().window);
        if(f != null) {
            Path file = f.toPath();
            if (Files.exists(file) && !Files.isDirectory(file) && new ConfirmOverwriteAlert(Monoglot.getMonoglot().window,
                    controller.resources.getString("dialog.confirmOverwrite.text")).showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                project.setSaveFile(file);
            } else project.setSaveFile(file);
            controller.setLocalStatus(project.save()?"app.status.saved":"app.status.saveFailed");
        }
    }

    public static void openProject(MonoglotController controller) {
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

    public static void recoverWorkingDirectory(MonoglotController controller){
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

    public static void newProject(MonoglotController controller){
        try {
            Monoglot.getMonoglot().newProject();
            controller.setProjectControlsEnabled(true);
        } catch (IOException e){
            //TODO: tell the user.
        }
    }

    /**
     * Zips and saves <code>workingDirectory</code> to <code>saveLocation</code>, overwriting if necessary.
     * @return true if successful.
     */
    public static boolean save(Path workingDirectory, Path saveLocation){
        try {
            URI zipPath = URI.create("jar:file:" + saveLocation.toAbsolutePath().toString());
            FileSystem zip = FileSystems.newFileSystem(zipPath, ZIP_FILE_SETTINGS);
            //TODO
        } catch(IOException e){
            return false;
        }
        return true;
    }

    public static void nuke(Path workingDirectory) {
        try {
            Files.walkFileTree(workingDirectory, new DirectoryDeleter());
        } catch(Exception e){
            // hope the OS cleans it up later.
            //TODO: try OS-specific delete method.
        }
    }
}

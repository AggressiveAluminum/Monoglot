package aa.monoglot.io;

import aa.monoglot.Monoglot;
import aa.monoglot.Project;
import aa.monoglot.ui.controller.MonoglotController;
import aa.monoglot.ui.dialog.ConfirmOverwriteAlert;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author cofl
 * @author Darren
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
     * @author Darren
     * @return true if successful.
     */
    /*public static boolean save(Path workingDirectory, Path saveLocation){
        try {
            URI zipPath = URI.create("jar:file:" + saveLocation.toAbsolutePath().toString());
            FileSystem zip = FileSystems.newFileSystem(zipPath, ZIP_FILE_SETTINGS);
            //TODO
        } catch(IOException e){
            return false;
        }
        return true;
    }*/
    public static boolean save(Path workingDirectory, Path saveLocation){
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(saveLocation.toFile()));

            File[] dirListing = workingDirectory.toFile().listFiles();
            for (int i = 0; i < dirListing.length; i++) {
                File input = new File(dirListing[i].getPath());
                FileInputStream fis = new FileInputStream(input);
                ZipEntry e = new ZipEntry(input.getName());
                out.putNextEntry(e);
                byte[] tmp = new byte[4 * 1024];
                int size = 0;
                while ((size = fis.read(tmp)) != -1) {
                    out.write(tmp, 0, size);
                }
                fis.close();
            }
            out.closeEntry();
            out.flush();
            out.close();
        } catch(IOException e){
            return false;
        }
        return true;
    }

    /**
     * @author Darren
     */
    public static boolean load(Path file, Path workingDirectory){
        try(ZipInputStream zipIs = new ZipInputStream(new BufferedInputStream(Files.newInputStream(file)))){
            ZipEntry zEntry = zipIs.getNextEntry();
            while(zEntry != null) {
                byte[] tmp = new byte[4 * 1024];
                OutputStream fos = Files.newOutputStream(workingDirectory.resolve(zEntry.getName()));
                int size = 0;
                while ((size = zipIs.read(tmp)) != -1) {
                    fos.write(tmp, 0, size);
                }
                fos.flush();
                fos.close();
                zEntry = zipIs.getNextEntry();
            }
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

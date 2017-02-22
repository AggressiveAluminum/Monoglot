package aa.monoglot;

import aa.monoglot.db.Database;
import aa.monoglot.io.IO;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author cofl
 * @date 2/17/2017
 */
public class Project {
    private Database database;
    private Path workingDirectory = Files.createTempDirectory("mglt");
    private Path saveFile;

    {
        System.out.println(workingDirectory.toString());
    }

    private BooleanProperty hasUnsavedChanges = new SimpleBooleanProperty(true);

    public Project() throws IOException {
        database = new Database(workingDirectory);
        //TODO
    }

    public Project(Path path) throws IOException {
        this(path, false);
    }

    public Project(Path path, boolean isDirectory) throws IOException {
        if(isDirectory){
            try {
                Files.delete(workingDirectory);
            } catch (Exception e){/* don't care */}
            workingDirectory = path;
        } else {
            if(!Files.exists(path) || Files.isDirectory(path))
                throw new FileNotFoundException(path.toAbsolutePath().toString());
            saveFile = path;
            hasUnsavedChanges.set(false);
        }
    }

    public boolean hasWorkingDirectory(){
        return workingDirectory != null && Files.exists(workingDirectory) && Files.isDirectory(workingDirectory);
    }

    public Path getWorkingDirectory() {
        return workingDirectory;
    }

    public void setSaveFile(Path path){
        this.saveFile = path;
        hasUnsavedChanges.set(true);
    }

    public boolean save(){
        if(IO.safeSave(database, workingDirectory, saveFile)){
            hasUnsavedChanges.set(false);
            return true;
        }
        return false;
    }

    public boolean hasSavePath(){
        return saveFile != null;
    }

    public boolean hasUnsavedChanges() {
        return hasUnsavedChanges.get();
    }

    /**
     * Closes and cleans up, but does not save, the project.
     */
    public void close() {
        database.close();
        if(hasWorkingDirectory())
            IO.nuke(workingDirectory);
        workingDirectory = null;
        saveFile = null;
        //TODO
    }
}
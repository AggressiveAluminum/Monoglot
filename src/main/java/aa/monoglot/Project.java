package aa.monoglot;

import aa.monoglot.db.Database;
import aa.monoglot.io.IO;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

/**
 * Entry point into project.
 * @author cofl
 * @date 2/17/2017
 */
public class Project {
    private Database database;
    private Path workingDirectory = Files.createTempDirectory("mglt");
    private Path saveFile;

    private BooleanProperty hasUnsavedChanges = new SimpleBooleanProperty(true);

    /**
     * Creates a new project with no attached save file.
     * @throws IOException
     */
    public Project() throws IOException, ClassNotFoundException {
        commonInit();
    }

    /**
     * Opens an existing project from a file.
     * @throws IOException
     */
    public Project(Path path) throws IOException, ClassNotFoundException {
        this(path, false);
    }

    /**
     * Opens an existing project, either from a file or directory.
     * @param path Path to the project file or directory.
     * @param isDirectory
     * @throws IOException
     */
    public Project(Path path, boolean isDirectory) throws IOException, ClassNotFoundException {
        if(isDirectory){
            try { // delete existing tmp dir, we don't use it.
                Files.delete(workingDirectory);
            } catch (Exception e){/* don't care */}
            workingDirectory = path;
        } else {
            if(!Files.exists(path) || Files.isDirectory(path))
                throw new FileNotFoundException(path.toAbsolutePath().toString());
            saveFile = path;
            IO.unzipToDirectory(saveFile, workingDirectory);
            hasUnsavedChanges.set(false);
        }

        commonInit();
    }

    /**
     * Common initialization for contructors.
     */
    private void commonInit() throws ClassNotFoundException {
        database = new Database(workingDirectory);
        System.err.println("> (◠‿◠✿) I'll wait for you here, sempai~~ " + workingDirectory.toString());
    }

    /**
     * Returns true if the project has a valid working directory, else false.
     */
    public boolean hasWorkingDirectory(){
        return workingDirectory != null && Files.exists(workingDirectory) && Files.isDirectory(workingDirectory);
    }

    /**
     * Gets the working directory.
     */
    public Path getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * Sets the file to save for all future saves.
     */
    public void setSaveFile(Path path){
        this.saveFile = path;
        hasUnsavedChanges.set(true);
    }

    /**
     * Saves the project to a file.
     * Writes the working directory contents to the save file.
     */
    public boolean save(){
        if(IO.safeSave(database, workingDirectory, saveFile)){
            hasUnsavedChanges.set(false);
            return true;
        }
        return false;
    }

    /**
     * Returns whether or not the project has a save file set.
     */
    public boolean hasSavePath(){
        return saveFile != null;
    }

    /**
     * Returns true if changes have been made and need to be saved, else false.
     */
    public boolean hasUnsavedChanges() {
        return hasUnsavedChanges.get();
    }

    /**
     * Closes and cleans up, but does not save, the project.
     */
    public void close() {
        try {
            database.close();
        } catch (SQLException e){/* TODO ??? */}
        if(hasWorkingDirectory())
            IO.nuke(workingDirectory);
        workingDirectory = null;
        saveFile = null;
    }
}
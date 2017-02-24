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
    private boolean amRecoveringProject = false;

    private BooleanProperty hasUnsavedChanges = new SimpleBooleanProperty(true);

    /**
     * Creates a project or opens an existing project, either from a file or directory.
     * If the given path is null, a new project is created with no attached save file.
     * If the given path is a file, the project is opened to the working directory.
     * If the given path is a directory, recovery mode is entered: the directory is used as the
     * working directory, and will not be cleaned up unless the project is explicitly saved.
     *
     * @param path Path to the project file or directory, or null for a new project.
     * @throws FileNotFoundException if the given path is not null and does not exist.
     * @throws IOException if file operations fail.
     * @throws ClassNotFoundException if the database driver cannot be loaded.
     * @throws SQLException if the database initialization fails.
     */
    public Project(Path path) throws FileNotFoundException, IOException, ClassNotFoundException, SQLException {
        if(path != null) {
            if (!Files.exists(path))
                throw new FileNotFoundException(path.toAbsolutePath().toString());
            if (Files.isDirectory(path)) {
                try {
                    Files.delete(workingDirectory);
                } catch (Exception e) {/* let the OS clean up its temps later */}
                workingDirectory = path;
                amRecoveringProject = true;
            } else {
                saveFile = path;
                IO.unzipToDirectory(saveFile, workingDirectory);
                hasUnsavedChanges.set(false);
            }
        }

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
        if(hasWorkingDirectory() && (!amRecoveringProject || !hasUnsavedChanges()))
            IO.nuke(workingDirectory);
        workingDirectory = null;
        saveFile = null;
    }
}
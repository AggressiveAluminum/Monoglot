package aa.monoglot.project;

import aa.monoglot.util.MonoglotEvents;
import aa.monoglot.project.db.Database;
import aa.monoglot.io.IO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public final class Project {
    private static Project instance;

    public static boolean isProjectOpen(){
        return instance != null;
    }

    public static Project getProject(){
        return instance;
    }

    public static void openProject() throws SQLException, IOException, ClassNotFoundException {
        instance = new Project();
    }

    public static void openProject(Path path) throws SQLException, IOException, ClassNotFoundException {
        instance = new Project(path);
    }

    // === INTERNALS ===
    private Path workingDirectory, saveFile;
    private Database database;
    private boolean amRecoveringProject = false;
    private boolean hasUnsavedChanges = true;

    /**
     * Creates a new Project.
     * @throws IOException if a working directory cannot be created.
     * @throws SQLException if the database initialization fails.
     * @throws ClassNotFoundException if the database driver cannot be loaded.
     */
    private Project() throws IOException, SQLException, ClassNotFoundException {
        workingDirectory = Files.createTempDirectory("mglt");
        database = new Database(workingDirectory);
        //TODO: log path properly
        System.err.println("> (◠‿◠✿) I'll wait for you here, sempai~~ " + workingDirectory.toString());
        MonoglotEvents.projectOpened();
    }

    /**
     * Opens an existing project from either a file or directory.
     * If the given path is a file, the project is opened to the working directory.
     * If the given path is a directory, recovery mode is entered: the directory is used as the
     * working directory, and will not be cleaned up unless the project is explicitly saved.
     *
     * @param path Path to the project file or directory
     * @throws FileNotFoundException if the given path is not null and does not exist.
     * @throws IOException if file operations fail.
     * @throws ClassNotFoundException if the database driver cannot be loaded.
     * @throws SQLException if the database initialization fails.
     */
    private Project(Path path) throws IOException, SQLException, ClassNotFoundException {
        if(path == null)
            throw new IllegalArgumentException();
        if(!Files.exists(path))
            throw new FileNotFoundException(path.toAbsolutePath().toString());
        if(Files.isDirectory(path)){
            workingDirectory = path;
            amRecoveringProject = true;
        } else {
            workingDirectory = Files.createTempDirectory("mglt");
            saveFile = path;
            IO.unzipToDirectory(saveFile, workingDirectory);
            hasUnsavedChanges = false;
        }

        database = new Database(workingDirectory);
        //TODO: log path properly
        System.err.println("> (◠‿◠✿) I'll wait for you here, sempai~~ " + workingDirectory.toString());
        MonoglotEvents.projectOpened();
    }

    public Path getWorkingDirectory(){
        return workingDirectory;
    }

    public Database getDatabase(){
        return database;
    }

    public boolean hasUnsavedChanges(){
        return hasUnsavedChanges;
    }

    /**
     * Sets the file to save to for all future saves.
     */
    public void setSaveFile(Path path){
        saveFile = path;
        hasUnsavedChanges = true;
    }

    /**
     * Returns whether or not the project has a save file set.
     */
    public boolean hasSavePath(){
        return saveFile != null;
    }

    /**
     * Saves the project to a file.
     * Writes the working directory contents to the save file.
     */
    public boolean save(){
        try {
            if (IO.safeSave(database, workingDirectory, saveFile)) {
                hasUnsavedChanges = false;
                return true;
            }
        } catch (SQLException e){/* return false; */}
        return false;
    }

    /**
     * Cleans up and disposes the current project.
     * <br/><b>Does not check if saving needs to be done!</b>
     */
    public void close(){
        try {
            database.close();
        } catch(SQLException e){
            //TODO: something about this
        }

        if(!(amRecoveringProject || hasUnsavedChanges))
            IO.nuke(workingDirectory);
        workingDirectory = null;
        saveFile = null;
        MonoglotEvents.projectClosed();
    }
}

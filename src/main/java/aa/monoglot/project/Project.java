package aa.monoglot.project;

import aa.monoglot.misc.keys.LocalizationKey;
import aa.monoglot.misc.keys.LogString;
import aa.monoglot.project.db.Database;
import aa.monoglot.project.db.Type;
import aa.monoglot.project.io.IO;
import aa.monoglot.util.BackedSettings;
import aa.monoglot.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public class Project {
    private static Project project;

    public static boolean isOpen(){
        return project != null;
    }
    /**
     * Returns the open project instance, or null if there isn't one open.
     */
    public static Project getProject() {
        return project;
    }
    /**
     * Creates a new Project.
     * @throws IOException if a working directory cannot be created.
     * @throws SQLException if the database initialization fails.
     * @throws ClassNotFoundException if the database driver cannot be loaded.
     */
    public static void newProject() throws SQLException, IOException, ClassNotFoundException {
        project = new Project(null);
        //TODO
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
    public static void openProject(Path path) throws SQLException, IOException, ClassNotFoundException {
        project = new Project(path);
        //TODO
    }

    // === INTERNALS ===
    private Path savePath, workingPath;
    private boolean amRecoveringProject = false;
    private boolean hasUnsavedChanges = true;
    private final Database database;
    private final BackedSettings<ProjectKey> settings;

    /**
     * Creates a new project, opens an existing project from either a file, or recovers a project
     * from a directory.
     * If the given path is null, a new project is created.
     * If the given path is a file, the project is opened to a new working directory.
     * If the given path is a directory, recovery mode is entered: the directory is used as the
     * working directory, and will not be cleaned up unless the project is explicitly saved.
     *
     * @param path Path to the project file or directory
     * @throws FileNotFoundException if the given path is not null and does not exist.
     * @throws IOException if file operations fail.
     * @throws ClassNotFoundException if the database driver cannot be loaded.
     * @throws SQLException if the database initialization fails.
     */
    private Project(final Path path) throws IOException, SQLException, ClassNotFoundException {
        if(path == null){
            workingPath = Files.createTempDirectory("mglt");
        } else {
            if(!Files.exists(path))
                throw new FileNotFoundException(path.toString());
            if(Files.isDirectory(path)){
                Log.info(LogString.PROJECT_RECOVERING, path.toAbsolutePath().toString());
                workingPath = path;
                amRecoveringProject = true;
            } else {
                Log.info(LogString.PROJECT_OPENING, path.toAbsolutePath().toString());
                workingPath = Files.createTempDirectory("mglt");
                savePath = path;
                IO.unzipToDirectory(savePath, workingPath);
                hasUnsavedChanges = false;
            }
        }

        database = new Database(workingPath);
        settings = new BackedSettings<>(workingPath.resolve("settings.properties"));

        if(path == null){
            Type.populateDefaults(database);
        }

        Log.info(LogString.PROJECT_PATH, workingPath.toString());
    }

    public BackedSettings<ProjectKey> getSettings() {
        return settings;
    }
    public Database getDatabase(){
        return database;
    }

    /**
     * Returns whether or not the project has a save file set.
     */
    public boolean hasSavePath() {
        return savePath != null;
    }

    /**
     * Sets the file to save to for all future saves.
     */
    public void setSavePath(Path savePath) {
        this.savePath = savePath;
        hasUnsavedChanges = true;
    }

    public boolean isSaveNeeded() {
        return amRecoveringProject || hasUnsavedChanges;
    }

    /**
     * Saves the project to a file.
     * Writes the working directory contents to the save file.
     */
    public boolean save() throws SQLException {
        if(IO.safeSave(database, workingPath, savePath)){
            hasUnsavedChanges = false;
            return true;
        }
        return false;
    }

    /**
     * Marks that there are changes made that need to be saved.
     */
    public void markSaveNeeded(){
        hasUnsavedChanges = true;
    }

    /**
     * Cleans up and disposes the current project.
     * <br/><b>Does not check if saving needs to be done!</b>
     */
    public void close() {
        try {
            database.close();
        } catch(SQLException e){
            //TODO
        }

        if(!isSaveNeeded())
            IO.nuke(workingPath);
        project = null;
        //TODO
    }
}

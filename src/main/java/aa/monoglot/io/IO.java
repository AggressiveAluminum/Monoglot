package aa.monoglot.io;

import aa.monoglot.db.Database;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.Map;

/**
 * Generic IO Utilities for saving/loading/cleaning up projects and project files.
 *
 * TODO: re-write these in a non-blocking fashion.
 * @author cofl
 * @date 2/22/2017
 */
public class IO {
    private static final Map<String, String> ZIP_CREATE_MAP = Collections.singletonMap("create", "true");
    private static final Map<String, String> ZIP_EMPTY_MAP = Collections.emptyMap();

    /**
     * Zips the given folder into a new zip file and returns the path to that zip file.
     * If the save fails, then <code>null</code> is returned.
     * <br/>Uses {@linkplain FileTreeCopier}
     */
    public static Path zipFolder(Path workingDirectory) throws IOException {
        Path tmp = Files.createTempFile("mglt-save-", ".zip");
        Files.deleteIfExists(tmp);
        URI tmpURI = URI.create("jar:" + tmp.toUri());
        try(FileSystem zip = FileSystems.newFileSystem(tmpURI, ZIP_CREATE_MAP)) {
            Files.walkFileTree(workingDirectory, new FileTreeCopier(workingDirectory, zip.getPath("/")));
        }
        return tmp;
    }

    /**
     * Unzips zip file <code>zip</code> to directory <code>workingDirectory</code>.
     * <br/>Replaces {@linkplain aa.monoglot.io.SaveLoad#load}
     * <br/>Uses {@linkplain FileTreeCopier}
     * @throws IOException
     */
    public static void unzipToDirectory(Path zip, Path workingDirectory) throws IOException {
        URI zipURI = URI.create("jar:" + zip.toUri());
        try (FileSystem zipFS = FileSystems.newFileSystem(zipURI, ZIP_EMPTY_MAP)){
            Path root = zipFS.getPath("/");
            Files.walkFileTree(root, new FileTreeCopier(root, workingDirectory));
        }
    }

    /**
     * Safely saves the project to a temporary file, then moves it, to prevent corruption of the existing project.
     * <br/>Replaces {@linkplain aa.monoglot.io.SaveLoad#save}
     * <br/>TODO: Am I being paranoid? It may be more efficient to just write straight to the thing.
     * @return True if the save and move were successful, else false.
     */
    public static boolean safeSave(Database database, Path workingDirectory, Path saveLocation){
        database.pause();
        try {
            Path tmp = zipFolder(workingDirectory);
            Files.move(tmp, saveLocation, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(tmp);
        } catch (IOException e){
            // uh-oh
            database.resume();
            return false;
        }
        database.resume();
        return true;
    }

    /**
     * Deletes the given directory with all contents.
     * <br/>Uses {@linkplain DirectoryDeleter}
     */
    public static void nuke(Path workingDirectory) {
        try {
            System.err.println("HAHAHAHAHAHA: " + workingDirectory.toString());
            Files.walkFileTree(workingDirectory, new DirectoryDeleter());
        } catch(Exception e){
            // hope the OS cleans it up later.
            //TODO: try OS-specific delete method.
        }
    }
}

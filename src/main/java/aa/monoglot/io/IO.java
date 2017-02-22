package aa.monoglot.io;

import aa.monoglot.db.Database;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
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
    private static final Map<String, String> ZIP_CREATE_MAP = Collections.singletonMap("create", "true");
    private static final Map<String, String> ZIP_EMPTY_MAP = Collections.emptyMap();

    public static Path zipFolder(Path workingDirectory){
        Path tmp;
        try {
            tmp = Files.createTempFile("mglt-save-", ".zip");
            Files.deleteIfExists(tmp);
            URI tmpURI = URI.create("jar:" + tmp.toUri());
            try (FileSystem zip = FileSystems.newFileSystem(tmpURI, ZIP_CREATE_MAP)) {
                Files.walkFileTree(workingDirectory, new FileTreeCopier(workingDirectory, zip.getPath("/")));
            }
        } catch(IOException e){
            return null;
        }
        return tmp;
    }

    public static void unzipToDirectory(Path zip, Path workingDirectory) throws IOException {
        URI zipURI = URI.create("jar:" + zip.toUri());
        try (FileSystem zipFS = FileSystems.newFileSystem(zipURI, ZIP_EMPTY_MAP)){
            Path root = zipFS.getPath("/");
            Files.walkFileTree(root, new FileTreeCopier(root, workingDirectory));
        }
    }

    /**
     * Zips and saves <code>workingDirectory</code> to <code>saveLocation</code>, overwriting if necessary.
     * @deprecated Use safeSave for maximum safety.
     * @author Darren
     * @see #safeSave
     * @return true if successful.
     */
    @Deprecated
    public static boolean save(Path workingDirectory, Path saveLocation){
        try(ZipOutputStream out = new ZipOutputStream(new FileOutputStream(saveLocation.toFile()))){
            File[] dirListing = workingDirectory.toFile().listFiles();
            if(dirListing == null) {
                for (int i = 0; i < dirListing.length; i++) {
                    File input = new File(dirListing[i].getPath());
                    try(FileInputStream fis = new FileInputStream(input)){
                        ZipEntry e = new ZipEntry(input.getName());
                        out.putNextEntry(e);
                        byte[] tmp = new byte[4 * 1024];
                        int size = 0;
                        while ((size = fis.read(tmp)) != -1) {
                            out.write(tmp, 0, size);
                        }
                    }
                    out.closeEntry();
                }
                out.flush();
            }
        } catch(IOException e){
            return false;
        }
        return true;
    }

    /**
     * Safely saves the project to a temporary file, then moves it, to prevent corruption of the existing project.
     * @see #save
     * @return True if the save and move were successful, else false.
     */
    public static boolean safeSave(Database database, Path workingDirectory, Path saveLocation){
        database.pause();
        try {
            Path tmp = zipFolder(workingDirectory);
            if(tmp != null) {
                Files.move(tmp, saveLocation, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.REPLACE_EXISTING);
                Files.deleteIfExists(tmp);
            }
        } catch (IOException e){
            database.resume();
            return false;
        }
        database.resume();
        return true;
    }

    /**
     * @author Darren
     */
    @Deprecated
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
            System.err.println("HAHAHAHAHAHA: " + workingDirectory.toString());
            Files.walkFileTree(workingDirectory, new DirectoryDeleter());
        } catch(Exception e){
            // hope the OS cleans it up later.
            //TODO: try OS-specific delete method.
        }
    }
}

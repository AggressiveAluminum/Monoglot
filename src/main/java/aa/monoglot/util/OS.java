package aa.monoglot.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author cofl
 * @date 3/1/2017
 */
public class OS {
    public static final OSType OS_TYPE;
    public static final Path SETTINGS_DIRECTORY;
    static {
        String os = System.getProperty("os.name").toUpperCase();
        if(os.contains("WIN")) {
            OS_TYPE = OSType.WINDOWS;
            SETTINGS_DIRECTORY = Paths.get(System.getenv("APPDATA"), "Monoglot");
        } else if(os.contains("MAC")) {
            OS_TYPE = OSType.MACOS;
            SETTINGS_DIRECTORY = Paths.get(System.getProperty("user.home"), "Library", "Application Support", "monoglot");
        } else {
            OS_TYPE = OSType.OTHER;
            SETTINGS_DIRECTORY = Paths.get(System.getProperty("user.home"), ".monoglot");
        }
    }

    public static void verify(){
        try {
            if (!Files.exists(SETTINGS_DIRECTORY)) {
                Files.createDirectories(SETTINGS_DIRECTORY);
            } else if (!Files.isDirectory(SETTINGS_DIRECTORY)) {
                Files.move(SETTINGS_DIRECTORY, SETTINGS_DIRECTORY.resolveSibling(".monoglot.file"));
                Files.createDirectory(SETTINGS_DIRECTORY);
            }
        } catch(IOException e){
            // be sure to do Files.exists whenever you use the path.
        }
    }
}

enum OSType{
    WINDOWS, MACOS, OTHER
}
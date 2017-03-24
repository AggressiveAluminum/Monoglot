package aa.monoglot.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simple utility functions.
 */
public final class UT {
    /**
     * String null coallesce to empty string.
     */
    public static String c(String o){
        if(o == null)
            return "";
        return o;
    }

    /**
     * Generic null coallesce to some default.
     */
    public static <T> T c(T o, T def){
        if(o == null)
            return def;
        return o;
    }

    /**
     * Compare two objects, taking nulls into account.
     */
    public static <T> boolean nc(T a, T b){
        if(a == null)
            return b == null;
        return a.equals(b);
    }

    public static Path forceAssureDirectory(Path directory) throws IOException {
        if(Files.isRegularFile(directory))
            Files.delete(directory);
        if(!Files.exists(directory))
            Files.createDirectories(directory);
        return directory;
    }
}
